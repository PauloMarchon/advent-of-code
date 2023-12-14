import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static final Pattern acharSimbolos = Pattern.compile("[^A-Za-z0-9\\s.]");
    static final Pattern acharNumeros = Pattern.compile("\\b\\d+\\b");

    public static void main(String[] args) throws Exception {
        List<String> input = Files.readAllLines(Path.of("input.txt"));

        int soma = calculaTotalDosNumerosValidos(input);

        System.out.println(soma); //537732

        int marcha = calculaMarchas(input);

        System.out.println(marcha); //84883664
    }

    public static int calculaMarchas(List<String> input) {
        int soma = 0;
        for (int i = 0; i < input.size(); i++) {
            List<PosicaoAsteristico> posicaoAsteristicos = retornaPosicaoDosAsteristicosNaLinha(input.get(i));
            List<String> linhasASerAnalisadas;

            if (i == 0) {
                linhasASerAnalisadas = input.subList(i, i + 2);
            } else if (i <= input.size() - 2){
                linhasASerAnalisadas = input.subList(i - 1, i + 2);
            } else {
                linhasASerAnalisadas = input.subList(i - 1, i);
            }

            for (PosicaoAsteristico pa : posicaoAsteristicos) {
                soma += verificaAsteristico(linhasASerAnalisadas, pa);
            }
        }
        return soma;
    }

    public static int verificaAsteristico(List<String> linhas, PosicaoAsteristico pa) {
        int soma = 0;
        List<Integer> numeros = new ArrayList<>();
        for (String s : linhas) {
          numeros.addAll(verificaSePossuiNumeros(s, pa.posicao()));
        }

        for (int i =0; i < numeros.size() - 1; i++){
           soma += numeros.get(i) * numeros.get(i+1);
           i++;
        }
        return soma;
    }

    public static List<Integer> verificaSePossuiNumeros(String linha, int posicao) {
        int inicio = 0;
        int fim = 0;
        List<NumerosAdjacentes> numeros = new ArrayList<>();
        List<Integer> numerosValidos = new ArrayList<>();

        if (posicao == 0) {
            fim = posicao + 1;
        } else if (posicao == 139) {
            inicio = posicao - 1;
            fim = posicao;
        } else {
            inicio = posicao - 1;
            fim = posicao + 1;
        }

        Matcher matcher = acharNumeros.matcher(linha);

        while (matcher.find()){
            List<Integer> posicoes = new ArrayList<>();
            int numeroEncontrado = Integer.parseInt(matcher.group());
            posicoes.add(matcher.start());
            posicoes.add(matcher.end() - 1);

            numeros.add(new NumerosAdjacentes(numeroEncontrado, posicoes));
        }

        for (NumerosAdjacentes na : numeros) {
            if (verificaNumeros(na.posicoes(), inicio, fim))
                numerosValidos.add(na.numero);
        }
        return numerosValidos;
    }

    public static boolean verificaNumeros(List<Integer> listaNumeros, int intervaloInicial, int intervaloFinal) {
        for (int numero : listaNumeros) {
            if (numero >= intervaloInicial && numero <= intervaloFinal)
                return true;
        }
        return false;
    }


    public static List<PosicaoAsteristico> retornaPosicaoDosAsteristicosNaLinha(String texto) {
        List<PosicaoAsteristico> posicao = new ArrayList<>();

        for (int i = 0; i < texto.length(); i++){
            char caracter = texto.charAt(i);
            if (caracter == '*')
                posicao.add(new PosicaoAsteristico(i));
        }
        return posicao;
}

    public static int calculaTotalDosNumerosValidos(List<String> linhas) {
        int soma = 0;
        for (int i = 0; i < linhas.size(); i++) {
            List<PosicaoNumero> posicaoNumeros = retornaNumeroComSuaPosicao(linhas.get(i));
            List<String> linhasASerAnalisadas;

            if (i == 0) {
                linhasASerAnalisadas = linhas.subList(i, i + 2);
            } else if (i <= linhas.size() - 2){
                linhasASerAnalisadas = linhas.subList(i - 1, i + 2);
            } else {
                linhasASerAnalisadas = linhas.subList(i - 1, i);
            }

            for (PosicaoNumero pn : posicaoNumeros) {
                if (verificaLinhas(linhasASerAnalisadas, pn))
                    soma += pn.numero;
            }
        }
        return soma;
    }

    public static List<PosicaoNumero> retornaNumeroComSuaPosicao(String linha) {
        List<PosicaoNumero> posicaoNumeros = new ArrayList<>();
        Matcher matcher = acharNumeros.matcher(linha);

            while (matcher.find()){
                posicaoNumeros.add(new PosicaoNumero(
                        Integer.parseInt(matcher.group()),
                        matcher.start(),
                        matcher.end() - 1));
            }
        return posicaoNumeros;
    }

    public static boolean verificaLinhas(List<String> linha, PosicaoNumero posicaoNumero){
        for (String s : linha) {
            if (verificaSePossuiSimboloAdjacente(s, posicaoNumero.inicio, posicaoNumero.fim))
                return true;
        }
        return false;
    }

    public static boolean verificaSePossuiSimboloAdjacente(String linha, int inicioAdjacente, int fimAdjacente) {
        int inicio = 0;
        int fim = 0;

        if (inicioAdjacente == 0) {
            fim = fimAdjacente + 2;
        } else if (fimAdjacente == 139) {
            inicio = inicioAdjacente - 1;
            fim = fimAdjacente;
        } else {
            inicio = inicioAdjacente - 1;
            fim = fimAdjacente + 2;
        }
        Matcher matcherAdjacente = acharSimbolos.matcher(linha).region(inicio, fim);

        return matcherAdjacente.find();
    }

    public record PosicaoNumero(int numero, int inicio, int fim){}
    public record PosicaoAsteristico(int posicao){};
    public record NumerosAdjacentes(int numero, List<Integer> posicoes){}
}