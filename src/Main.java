import java.io.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Main {

    private static final String FICHERO_TXT = "datos/peliculas.txt";
    private static final String FICHERO_XML = "peliculas.xml";

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);


        List<Pelicula> peliculas = leerPeliculas(FICHERO_TXT);


        int opcion = -99;

        do {


            System.out.println("===== CATÁLOGO =====");
            System.out.println("1. Mostrar todas");
            System.out.println("2. Buscar por género");
            System.out.println("3. Buscar por título");
            System.out.println("4. Exportar a XML");
            System.out.println("5. Importar XML");
            System.out.println("0. Salir");


            opcion = Integer.parseInt(sc.nextLine().trim());

            switch (opcion) {
                case 1:
                    mostrarTodas(peliculas);
                    break;
                case 2:
                    buscarPorGenero(peliculas, sc);
                    break;
                case 3:
                    buscarPorTitulo(peliculas, sc);
                    break;
                case 4:
                    exportarXML(peliculas, FICHERO_XML);
                    break;
                case 5:
                    List<Pelicula> importadas = importarXML(FICHERO_XML);
                    System.out.println(" peliculas importadas desde XML");
                    for (Pelicula p : importadas) {
                        System.out.println(p);
                    }
                    break;
                case 0:
                    System.out.println("Saliendo del programa");
                    break;
                default:
                    System.out.println("Eso no vale");
            }
        } while (opcion != 0);


        sc.close();
    }







    public static List<Pelicula> leerPeliculas(String nombreFichero) {
        List<Pelicula> lista = new ArrayList<>();

        try {
            FileReader fr = new FileReader(nombreFichero);
            BufferedReader br = new BufferedReader(fr);

            String linea = br.readLine();


            while (linea != null) {
                String[] campos = linea.split("\\|");

                int id = Integer.parseInt(campos[0]);
                String nombre = campos[1];
                String director = campos[2];
                int year = Integer.parseInt(campos[3]);
                String tipo = campos[4];

                Pelicula p = new Pelicula(id, nombre, director, year, tipo);


                lista.add(p);

                linea = br.readLine();
            }

            br.close();
            System.out.println("fichero se ha leido " + lista.size());

        } catch (FileNotFoundException e) {
            System.out.println("no se encuentra fichero " + nombreFichero);
        } catch (IOException e) {
            System.out.println("error mumalo " + e.getMessage());
        }

        return lista;
    }





    private static void mostrarTodas(List<Pelicula> peliculas) {
        System.out.println("lista de nombres ");
        for (Pelicula p : peliculas) {
            System.out.println("- " + p.getNombre());
        }
    }






    private static void buscarPorGenero(List<Pelicula> peliculas, Scanner sc) {

        List<String> generos = new ArrayList<>();
        for (Pelicula p : peliculas) {
            if (!generos.contains(p.getTipo())) {
                generos.add(p.getTipo());
            }
        }

        System.out.println(" tipos de peliculas");
        for (int i = 0; i < generos.size(); i++) {
            System.out.println((i + 1) + ". " + generos.get(i));
        }

        System.out.print("elige el numero del genero ");
        int opcion;
        try {
            opcion = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Opción no válida.");
            return;
        }

        if (opcion < 1 || opcion > generos.size()) {
            System.out.println("Opción fuera de rango.");
            return;
        }

        String generoElegido = generos.get(opcion - 1);

        System.out.println(" peliculas de genero " + generoElegido);
        for (Pelicula p : peliculas) {
            if (p.getTipo().equalsIgnoreCase(generoElegido)) {
                System.out.println(p);
            }
        }
    }





    private static void buscarPorTitulo(List<Pelicula> peliculas, Scanner sc) {
        System.out.print("Introduce el título de la pelicula ");
        String texto = sc.nextLine().trim().toLowerCase();

        System.out.println("\n--- Resultados ---");
        boolean encontrada = false;

        for (Pelicula p : peliculas) {
            if (p.getNombre().toLowerCase().contains(texto)) {
                System.out.println(p);
                encontrada = true;
            }
        }

        if (!encontrada) {
            System.out.println("no lo has puesto bien");
        }
    }






    public static void exportarXML(List<Pelicula> peliculas, String nombreFichero) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element raiz = doc.createElement("catalogo");
            doc.appendChild(raiz);

            for (Pelicula p : peliculas) {
                Element elemPelicula = doc.createElement("pelicula");
                elemPelicula.setAttribute("id", String.valueOf(p.getId()));

                Element titulo = doc.createElement("titulo");
                titulo.appendChild(doc.createTextNode(p.getNombre()));
                elemPelicula.appendChild(titulo);

                Element director = doc.createElement("director");
                director.appendChild(doc.createTextNode(p.getDirector()));
                elemPelicula.appendChild(director);

                Element anio = doc.createElement("anio");
                anio.appendChild(doc.createTextNode(String.valueOf(p.getYear())));
                elemPelicula.appendChild(anio);

                Element genero = doc.createElement("genero");
                genero.appendChild(doc.createTextNode(p.getTipo()));
                elemPelicula.appendChild(genero);

                raiz.appendChild(elemPelicula);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(nombreFichero));
            transformer.transform(source, result);

            System.out.println("Exportación completada. Fichero generado: " + nombreFichero);

        } catch (ParserConfigurationException | javax.xml.transform.TransformerException e) {
            System.out.println("Error al generar el XML: " + e.getMessage());
        }
    }




    public static List<Pelicula> importarXML(String nombreFichero) {
        List<Pelicula> lista = new ArrayList<>();
        File fichero = new File(nombreFichero);

        if (!fichero.exists()) {
            System.out.println("Error: no existe el fichero '" + nombreFichero
                    + "'. Genera primero el XML con la opción 4.");
            return lista;
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(fichero);
            doc.getDocumentElement().normalize();

            NodeList nodos = doc.getElementsByTagName("pelicula");

            for (int i = 0; i < nodos.getLength(); i++) {
                Node nodo = nodos.item(i);

                if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                    Element elem = (Element) nodo;

                    int id = Integer.parseInt(elem.getAttribute("id"));
                    String titulo = elem.getElementsByTagName("titulo").item(0).getTextContent();
                    String director = elem.getElementsByTagName("director").item(0).getTextContent();
                    int anio = Integer.parseInt(elem.getElementsByTagName("anio").item(0).getTextContent());
                    String genero = elem.getElementsByTagName("genero").item(0).getTextContent();

                    lista.add(new Pelicula(id, titulo, director, anio, genero));
                }
            }

            System.out.println("Importación completada. " + lista.size() + " películas leídas de " + nombreFichero);

        } catch (ParserConfigurationException | SAXException | IOException | NumberFormatException e) {
            System.out.println("Error al importar el XML: " + e.getMessage());
        }

        return lista;
    }
}