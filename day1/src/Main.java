import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    private static final String[] numeros = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
    private static final String teste = "onetwo134p";

    public static void main(String[] args) throws IOException {

        String input = Files.readString(Path.of("input.txt"));

        //Part 1
        int resultado = input.lines().mapToInt(Main::pegarValorDaLinha).sum();
        System.out.println(resultado); //54597

        //Part 2
        int novoResultado = input.lines().mapToInt(Main::alterarValorEscritoDaLinha).sum();
        System.out.println(novoResultado);
    }

    private static int pegarValorDaLinha(String linha) {
         int primeiroNumero = 0;
         int ultimoNumero = 0;

         for (char c : linha.toCharArray()) {
             if (Character.isDigit(c) && primeiroNumero == 0) {
                    primeiroNumero = Character.getNumericValue(c);
             }
             if (Character.isDigit(c)) {
                 ultimoNumero = Character.getNumericValue(c);
             }
         }
    return Integer.parseInt(primeiroNumero + "" + ultimoNumero);
    }

    // eightwo
    //

    private static int alterarValorEscritoDaLinha(String linha) {
        String linhaTemp = linha;

        for (int i = 0; i < numeros.length; i++) {
            linhaTemp = linhaTemp.replaceAll(numeros[i], numeros[i] + (i + 1) + numeros[i]);
        }
        return pegarValorDaLinha(linhaTemp);
    }
}