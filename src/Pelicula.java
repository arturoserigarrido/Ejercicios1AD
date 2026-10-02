public class Pelicula {
    private int id;
    private String nombre;
    private String director;
    private int year;
    private String tipo;

    public Pelicula(int id, String nombre, String director, int year, String tipo){
        this.id = id;
        this.nombre = nombre;
        this.director = director;
        this.year = year;
        this.tipo = tipo;

    }

    public int getId(){
        return id;
    }

    public String getNombre(){
        return nombre;
    }

    public String getDirector(){
        return director;
    }

    public int getYear(){
        return year;
    }

    public String getTipo(){
        return tipo;
    }

    public String toString() {
        return "ID: " + id +
                " | Título: " + nombre +
                " | Director: " + director +
                " | Año: " + year +
                " | Género: " + tipo;
    }
}

