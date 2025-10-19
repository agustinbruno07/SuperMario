package GUI;

public class EstadoJuego {
    private static boolean cofreAbierto = false;
    private static boolean puertaAbierta = false;

    public static boolean isCofreAbierto() {
        return cofreAbierto;
    }

    public static void setCofreAbierto(boolean abierto) {
        cofreAbierto = abierto;
    }

    public static boolean isPuertaAbierta() {
        return puertaAbierta;
    }

    public static void setPuertaAbierta(boolean abierta) {
        puertaAbierta = abierta;
    }
}