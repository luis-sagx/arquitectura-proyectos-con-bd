package ec.edu.monster.service;

import ec.edu.monster.dao.LoginRepository;

/** Application service for credential validation. */
public class Login {

    private final LoginRepository repository;

    public Login() {
        this(new LoginRepository());
    }

    Login(LoginRepository repository) {
        this.repository = repository;
    }

    public boolean IniciarSesion(String nombreUsuario, String contrasena) {
        return repository.validarCredenciales(nombreUsuario, contrasena);
    }
}
