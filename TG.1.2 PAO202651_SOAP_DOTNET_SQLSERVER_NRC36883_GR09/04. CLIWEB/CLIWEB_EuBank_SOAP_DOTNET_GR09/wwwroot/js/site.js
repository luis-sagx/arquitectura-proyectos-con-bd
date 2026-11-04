const SOAP = 'http://localhost:9091/Services/';
const LOGIN_NS = 'http://monster.edu.ec/eurekabank/login';
const MOVEMENTS_NS = 'http://monster.edu.ec/eurekabank/movimientos';
const ENVELOPE_NS = 'http://schemas.xmlsoap.org/soap/envelope/';
const $ = (id) => document.getElementById(id);
const state = { accounts: [], types: [], tab: 'cuentas' };
const escapeHtml = (value) => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));

async function soapCall(service, namespace, contract, operation, args = {}) {
  const parameters = Object.entries(args).map(([name, value]) => `<${name}>${escapeHtml(value ?? '')}</${name}>`).join('');
  const envelope = `<?xml version="1.0" encoding="utf-8"?><soap:Envelope xmlns:soap="${ENVELOPE_NS}"><soap:Body><${operation} xmlns="${namespace}">${parameters}</${operation}></soap:Body></soap:Envelope>`;
  const response = await fetch(`${SOAP}${service}`, { method: 'POST', headers: {
    'Content-Type': 'text/xml; charset=utf-8', SOAPAction: `"${namespace}/${contract}/${operation}"`
  }, body: envelope });
  const xml = new DOMParser().parseFromString(await response.text(), 'application/xml');
  const fault = [...xml.getElementsByTagName('*')].find(element => element.localName === 'faultstring');
  if (fault) throw new Error(fault.textContent || 'Error SOAP.');
  if (!response.ok) throw new Error(`Error del servidor SOAP (${response.status}).`);
  return xml;
}
function soapResult(xml, name) {
  const node = [...xml.getElementsByTagName('*')].find(element => element.localName === name);
  if (!node) throw new Error(`La respuesta SOAP no contiene ${name}.`);
  return node;
}
function soapText(node, name) { return [...node.children].find(child => child.localName === name)?.textContent || ''; }
function soapItems(xml, resultName, itemName) {
  return [...soapResult(xml, resultName).children].filter(item => item.localName === itemName);
}
function mapAccount(item) {
  return { codigo: soapText(item, 'Codigo'), moneda: soapText(item, 'Moneda'), saldo: Number(soapText(item, 'Saldo')),
    estado: soapText(item, 'Estado'), nombreCliente: soapText(item, 'NombreCliente'), emailCliente: soapText(item, 'EmailCliente'),
    telefonoCliente: soapText(item, 'TelefonoCliente') };
}
function mapMovement(item) {
  return { numero: Number(soapText(item, 'Numero')), ordenCreacion: Number(soapText(item, 'OrdenCreacion')),
    fecha: soapText(item, 'Fecha').slice(0, 10), tipo: soapText(item, 'Tipo'), importe: Number(soapText(item, 'Importe')),
    referencia: soapText(item, 'Referencia'), codigoCuenta: soapText(item, 'CodigoCuenta'),
    saldoActual: Number(soapText(item, 'SaldoActual')), nombreCliente: soapText(item, 'NombreCliente') };
}
async function api(path, options = {}) {
  const [resource, query] = path.split('?');
  const params = new URLSearchParams(query || '');
  if (resource === '/login') {
    const credentials = JSON.parse(options.body || '{}');
    const xml = await soapCall('LoginService.svc', LOGIN_NS, 'ILoginService', 'Login', credentials);
    return { success: soapResult(xml, 'LoginResult').textContent?.trim().toLowerCase() === 'true' };
  }
  if (resource === '/cuentas') {
    const xml = await soapCall('MovimientosService.svc', MOVEMENTS_NS, 'IMovimientosService', 'GetDatosCuentas');
    return soapItems(xml, 'GetDatosCuentasResult', 'CuentaData').map(mapAccount);
  }
  if (resource === '/tiposmovimiento') {
    const xml = await soapCall('MovimientosService.svc', MOVEMENTS_NS, 'IMovimientosService', 'GetTiposMovimiento');
    return soapItems(xml, 'GetTiposMovimientoResult', 'string').map(item => item.textContent || '');
  }
  if (resource === '/movimientos' && options.method === 'POST') {
    const movement = JSON.parse(options.body || '{}');
    const xml = await soapCall('MovimientosService.svc', MOVEMENTS_NS, 'IMovimientosService', 'ProcesarMovimiento', movement);
    const result = soapResult(xml, 'ProcesarMovimientoResult').children[0];
    return { success: soapText(result, 'Codigo') === '1', message: soapText(result, 'Mensaje') };
  }
  if (resource === '/movimientos') {
    const xml = await soapCall('MovimientosService.svc', MOVEMENTS_NS, 'IMovimientosService', 'GetMovimientos', { cuenta: params.get('cuenta') || '' });
    return soapItems(xml, 'GetMovimientosResult', 'MovimientoData').map(mapMovement);
  }
  throw new Error('Operación no disponible en el servicio SOAP.');
}const formatCurrency = (amount, code) => `${code === '01' ? 'S/' : '$'} ${Number(amount || 0).toFixed(2)}`;
const localDate = (isoDate) => {
  const [year, month, day] = String(isoDate || '').split('-').map(Number);
  return year && month && day ? new Date(year, month - 1, day).toLocaleDateString('es-EC', { year: 'numeric', month: '2-digit', day: '2-digit' }) : '-';
};

$('loginForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const button = $('loginButton');
  button.disabled = true; button.textContent = 'Validando…'; $('loginError').classList.add('hidden');
  const data = new FormData(event.currentTarget);
  try {
    const login = await api('/login', { method: 'POST', body: JSON.stringify({ usuario: data.get('usuario'), clave: data.get('clave') }) });
    if (!login.success) throw new Error('Usuario o contraseña incorrectos.');
    const [accounts, types] = await Promise.all([api('/cuentas'), api('/tiposmovimiento')]);
    state.accounts = accounts; state.types = types;
    $('loginView').classList.add('hidden'); $('sidebar').classList.remove('hidden'); $('dashboard').classList.remove('hidden');
    renderAccounts(); fillAccountSelects(); fillMovementTypes();
  } catch (error) {
    $('loginError').textContent = error.message || 'No se pudo conectar con el servidor SOAP.';
    $('loginError').classList.remove('hidden');
  } finally { button.disabled = false; button.textContent = 'Iniciar sesión'; }
});

$('logout').addEventListener('click', () => location.reload());
document.querySelectorAll('.tab-button').forEach(button => button.addEventListener('click', () => {
  state.tab = button.dataset.tab;
  document.querySelectorAll('.tab-button').forEach(item => item.classList.toggle('active', item === button));
  document.querySelectorAll('.tab-pane').forEach(pane => pane.classList.toggle('hidden', pane.id !== state.tab));
  if (state.tab === 'movimientos') loadMovements();
}));

function fillAccountSelects() {
  const accountOptions = state.accounts.map(account => `<option value="${escapeHtml(account.codigo)}">${escapeHtml(account.codigo)} - ${escapeHtml(account.nombreCliente)}</option>`).join('');
  $('selectCuenta').innerHTML = '<option value="">-- Todas las cuentas --</option>' + accountOptions;
  const currencies = [...new Set(state.accounts.map(account => account.moneda).filter(Boolean))];
  $('filtroMoneda').innerHTML = '<option value="">-- Todas --</option>' + currencies.map(currency => `<option value="${escapeHtml(currency)}">${escapeHtml(currency)}</option>`).join('');
}
function fillMovementTypes() {
  $('tipoMovimiento').innerHTML = '<option value="">-- Seleccionar Tipo --</option>' + state.types.map(type => `<option value="${escapeHtml(type)}">${escapeHtml(type)}</option>`).join('');
}

function renderAccounts() {
  const status = $('filtroEstado').value, currency = $('filtroMoneda').value, query = $('busquedaCliente').value.trim().toLocaleLowerCase('es');
  const accounts = state.accounts.filter(account => (!status || account.estado === status) && (!currency || account.moneda === currency) && (!query || String(account.nombreCliente).toLocaleLowerCase('es').includes(query)));
  $('cuentasLoading').classList.add('hidden'); $('cuentasError').classList.add('hidden');
  $('cuentasContent').classList.toggle('hidden', accounts.length === 0); $('cuentasEmpty').classList.toggle('hidden', accounts.length !== 0);
  $('cuentasTableBody').innerHTML = accounts.map(account => {
    const active = account.estado === 'ACTIVO';
    const badge = active ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800';
    return `<tr class="hover:bg-gray-50"><td class="text-sm font-medium text-gray-900">${escapeHtml(account.codigo)}</td><td class="text-sm text-gray-900">${escapeHtml(account.nombreCliente)}</td><td class="text-sm text-gray-500">${escapeHtml(account.emailCliente || '-')}</td><td class="text-sm text-gray-500">${escapeHtml(account.telefonoCliente || '-')}</td><td class="text-sm text-gray-900">${escapeHtml(account.moneda)}</td><td class="text-sm font-medium text-gray-900">${formatCurrency(account.saldo, account.moneda)}</td><td><span class="inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ${badge}">${active ? 'Activo' : 'Inactivo'}</span></td></tr>`;
  }).join('');
}
['filtroEstado', 'filtroMoneda'].forEach(id => $(id).addEventListener('change', renderAccounts));
$('busquedaCliente').addEventListener('input', renderAccounts);
$('selectCuenta').addEventListener('change', loadMovements);

async function loadMovements() {
  $('movimientosLoading').classList.remove('hidden'); $('movimientosContent').classList.add('hidden'); $('movimientosEmpty').classList.add('hidden'); $('movimientosError').classList.add('hidden');
  const accountCode = $('selectCuenta').value;
  try {
    const movements = await api('/movimientos' + (accountCode ? `?cuenta=${encodeURIComponent(accountCode)}` : ''));
    renderMovements(movements, accountCode);
  } catch (error) {
    $('movimientosError').textContent = `Error al cargar movimientos: ${error.message}`; $('movimientosError').classList.remove('hidden');
  } finally { $('movimientosLoading').classList.add('hidden'); }
}
function movementBadge(type) {
  const normalized = String(type || '').toLocaleLowerCase('es');
  const [label, color] = normalized.includes('deposit') ? ['Depósito', 'bg-green-100 text-green-800'] : normalized.includes('retiro') ? ['Retiro', 'bg-orange-100 text-orange-800'] : normalized.includes('cancel') ? ['Cancelar Cuenta', 'bg-red-100 text-red-800'] : normalized.includes('transfer') ? ['Transferencia', 'bg-slate-100 text-slate-800'] : normalized.includes('apertura') ? ['Apertura de Cuenta', 'bg-slate-100 text-slate-800'] : [type || 'Movimiento', 'bg-gray-100 text-gray-800'];
  return `<span class="inline-flex rounded-full px-2.5 py-0.5 text-xs font-medium ${color}">${escapeHtml(label)}</span>`;
}
function renderMovements(movements, accountCode) {
  const account = state.accounts.find(item => item.codigo === accountCode);
  $('movimientosTitle').textContent = accountCode ? `Movimientos de la Cuenta ${accountCode}${account ? ` - ${account.nombreCliente}` : ''}` : 'Todos los Movimientos';
  if (!movements?.length) {
    $('movimientosEmpty').textContent = accountCode ? `No hay movimientos para la cuenta ${accountCode}.` : 'No hay movimientos en ninguna cuenta.';
    $('movimientosEmpty').classList.remove('hidden'); return;
  }
  const sorted = [...movements].sort((a, b) => String(b.fecha || '').localeCompare(String(a.fecha || '')) || Number(b.ordenCreacion || 0) - Number(a.ordenCreacion || 0));
  $('movimientosTableBody').innerHTML = sorted.map(movement => {
    const currentAccount = state.accounts.find(item => item.codigo === movement.codigoCuenta);
    const accountLabel = accountCode ? `${accountCode}${currentAccount ? ` - ${currentAccount.nombreCliente}` : ''}` : `${movement.codigoCuenta}${currentAccount ? ` - ${currentAccount.nombreCliente}` : ''}`;
    return `<tr class="hover:bg-gray-50"><td class="text-sm text-gray-900">${escapeHtml(accountLabel)}</td><td class="text-sm font-medium text-gray-900">${escapeHtml(movement.numero)}</td><td class="text-sm text-gray-900">${localDate(movement.fecha)}</td><td>${movementBadge(movement.tipo)}</td><td class="text-sm text-gray-500">${escapeHtml(movement.referencia || '-')}</td><td class="text-sm font-medium text-gray-900">${formatCurrency(movement.importe, '02')}</td><td class="text-sm font-semibold text-green-600">${formatCurrency(movement.saldoActual, '02')}</td></tr>`;
  }).join('');
  $('movimientosContent').classList.remove('hidden');
}

$('btnCrearMovimiento').addEventListener('click', () => { $('modalError').classList.add('hidden'); $('modalCrearMovimiento').classList.remove('hidden'); });
$('btnCancelarModal').addEventListener('click', closeModal);
$('modalCrearMovimiento').addEventListener('click', event => { if (event.target === $('modalCrearMovimiento')) closeModal(); });
$('tipoMovimiento').addEventListener('change', () => {
  const type = $('tipoMovimiento').value, transfer = type === 'TRANSFERENCIA';
  $('divCuentaDestino').classList.toggle('hidden', !transfer); $('cuentaDestino').required = transfer; if (!transfer) $('cuentaDestino').value = '';
  const imageMap = { DEPOSITO: ['/images/Deposito.png', 'Depósito'], RETIRO: ['/images/Retiro.png', 'Retiro'], TRANSFERENCIA: ['/images/Transferencia.png', 'Transferencia'] };
  const image = imageMap[type]; $('imagenTipoMovimiento').classList.toggle('hidden', !image); $('imagenTipoMovimiento').classList.toggle('flex', !!image);
  if (image) { $('imgTipoMovimiento').src = image[0]; $('textoTipoMovimiento').textContent = image[1]; }
});
function closeModal() { $('modalCrearMovimiento').classList.add('hidden'); $('formCrearMovimiento').reset(); $('divCuentaDestino').classList.add('hidden'); $('cuentaDestino').required = false; $('imagenTipoMovimiento').classList.add('hidden'); $('imagenTipoMovimiento').classList.remove('flex'); $('modalError').classList.add('hidden'); }
$('formCrearMovimiento').addEventListener('submit', async event => {
  event.preventDefault(); $('modalError').classList.add('hidden');
  const form = new FormData(event.currentTarget), type = String(form.get('tipo')), origin = String(form.get('cuentaOrigen')), destination = String(form.get('cuentaDestino') || ''), amount = Number(form.get('importe'));
  if (!type || !origin || !Number.isFinite(amount) || amount <= 0 || (type === 'TRANSFERENCIA' && (!destination || destination === origin))) {
    $('modalError').textContent = 'Verifica el tipo, las cuentas y el monto ingresados.'; $('modalError').classList.remove('hidden'); return;
  }
  const submit = $('btnSubmitMovimiento'); submit.disabled = true; submit.textContent = 'Procesando…';
  try {
    const result = await api('/movimientos', { method: 'POST', body: JSON.stringify({ tipo: type, cuentaOrigen: origin, cuentaDestino: destination || null, importe: amount }) });
    if (result.success === false) throw new Error(result.message || 'No se pudo registrar el movimiento.');
    closeModal(); state.accounts = await api('/cuentas'); fillAccountSelects(); renderAccounts(); if (state.tab === 'movimientos') await loadMovements();
    window.alert('Movimiento creado exitosamente.');
  } catch (error) { $('modalError').textContent = error.message || 'Error al conectar con el servidor.'; $('modalError').classList.remove('hidden'); }
  finally { submit.disabled = false; submit.textContent = 'Crear Movimiento'; }
});
