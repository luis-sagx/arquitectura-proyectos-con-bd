
package ec.edu.monster.prueba;
import ec.edu.monster.model.MovimientoData;
import ec.edu.monster.service.EurekaService;
import java.util.List;

/**
 *
 * @author leona
 */
public class PruebaLeerMovimientos {

    public static void main(String[] args) {
        try {
            //dato de la prueba
            String cuenta = "00100001";
            //proceso
            EurekaService service = new EurekaService();
            List<MovimientoData> lista = service.leerMovimientos(cuenta);
            //reporte
            for (MovimientoData r : lista) {
                System.out.println(r.getNumero()+ " - " + r.getNumero()+ " - " + r.getFecha() + " - " + r.getTipo() + " - " + r.getImporte() + " - " + r.getReferencia());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
