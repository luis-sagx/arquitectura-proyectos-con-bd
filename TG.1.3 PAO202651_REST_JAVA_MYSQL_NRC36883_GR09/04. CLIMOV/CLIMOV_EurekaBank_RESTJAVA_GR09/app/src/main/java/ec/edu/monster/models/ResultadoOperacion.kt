package ec.edu.monster.models

data class ResultadoOperacion(
    val codigo: Int = 0,
    val mensaje: String = ""
) {
    fun esExito() = codigo == 1
}