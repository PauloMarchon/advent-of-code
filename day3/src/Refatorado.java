import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Refatorado {
    static final Pattern acharSimbolos = Pattern.compile("[^A-Za-z0-9\\s.]");
    static final Pattern acharNumeros = Pattern.compile("\\b\\d+\\b");

    public static void main(String[] args) throws Exception{
        List<String> input = Files.readAllLines(Path.of("input.txt"));

        int soma = somaOsNumerosComSimboloAdjacente(input);
        System.out.println(soma); //537732

        int marcha = calculaTotalDasMarchas(input);
        System.out.println(marcha); //84883664

    }

    public static int calculaTotalDasMarchas(List<String> input){
        int soma = 0;
        for(int i = 0; i < input.size(); i++){
            List<Integer> posicaoAsteriscos = capturaPosicaoDosAsteriscosNaLinhaCorrente(input.get(i));
            List<String> linhasASerAnalisadas = selecionaLinhaCorrenteComSuasAdjacentes(input, i);

            for(Integer posicao : posicaoAsteriscos){
                soma += calculaTotalDosNumerosQuePossuemAdjacenciaComAsterisco(linhasASerAnalisadas, posicao);
            }
        }
        return soma;
    }

    public static int calculaTotalDosNumerosQuePossuemAdjacenciaComAsterisco(List<String> linhas, int posicaoAsterisco){
        int soma = 0;
        List<Integer> numerosComAdjacencia = capturaOsNumerosQuePossuemAdjacenciaComAsteriscos(linhas, posicaoAsterisco);

        for(int i = 0; i < numerosComAdjacencia.size() - 1; i+=2){
            soma += numerosComAdjacencia.get(i) * numerosComAdjacencia.get(i+1);
        }
        return soma;
    }

     public static List<Integer> capturaOsNumerosQuePossuemAdjacenciaComAsteriscos(List<String> linhas, int posicaoAsterisco){
        EscopoDeAnalise escopoDeAnalise = defineEscopoDeAnaliseAdjacenteDoObjeto(posicaoAsterisco, posicaoAsterisco, true);
        List<Integer> numerosComAdjacencia = new ArrayList<>();

        for(String linha : linhas) {
            List<NumeroNaLinha> numeros = buscaTodosOsNumerosNaLinhaCorrente(linha);

            for(NumeroNaLinha numeroCorrente : numeros) {
                List<Integer> posicoesDoNumero = new ArrayList<>();
                posicoesDoNumero.add(numeroCorrente.inicioDaPosicaoNaLinha());
                posicoesDoNumero.add(numeroCorrente.terminoDaPosicaoNaLinha());
                
                if (posicaoDoNumeroEAdjacenteAoAsterisco(posicoesDoNumero, escopoDeAnalise)) 
                    numerosComAdjacencia.add(numeroCorrente.valorDoNumero());
            }
        }
        return numerosComAdjacencia;
    }

    public static boolean posicaoDoNumeroEAdjacenteAoAsterisco(List<Integer> posicoesDoNumero, EscopoDeAnalise escopoDeAnalise) {
        for(int posicao : posicoesDoNumero) {
            if (posicao >= escopoDeAnalise.inicioDoEscopo() && posicao <= escopoDeAnalise.fimDoEscopo())
                return true;
        }
        return false;
    }

    public static List<Integer> capturaPosicaoDosAsteriscosNaLinhaCorrente(String linha){
        List<Integer> posicaoAsteriscos = new ArrayList<>();

        for(int i = 0; i < linha.length(); i++){
            char caracterCorrente = linha.charAt(i);
            if (caracterCorrente == '*') {
                posicaoAsteriscos.add(i);
            }
        }
        return posicaoAsteriscos;
    }
    
    public static int somaOsNumerosComSimboloAdjacente(List<String> input){
        int soma = 0;
        for(int i = 0; i < input.size(); i++){
            List<NumeroNaLinha> numerosNaLinha = buscaTodosOsNumerosNaLinhaCorrente(input.get(i));
            List<String> linhasASerAnalisadas = selecionaLinhaCorrenteComSuasAdjacentes(input, i);

            for(NumeroNaLinha numero : numerosNaLinha) {
                if(numeroPossuiSimbolosAdjacentes(linhasASerAnalisadas, numero))
                    soma += numero.valorDoNumero();
            }
        }
        return soma;
    }

    public static List<String> selecionaLinhaCorrenteComSuasAdjacentes(List<String> input, int linhaCorrente){
        List<String> linhasASerAnalisadas;

        if (linhaCorrente == 0) {
            linhasASerAnalisadas = input.subList(linhaCorrente, linhaCorrente + 2);
        } else if (linhaCorrente <= input.size() - 2){
            linhasASerAnalisadas = input.subList(linhaCorrente - 1, linhaCorrente + 2);
        } else {
            linhasASerAnalisadas = input.subList(linhaCorrente - 1, linhaCorrente);
        }
        return linhasASerAnalisadas;
    }

    public static List<NumeroNaLinha> buscaTodosOsNumerosNaLinhaCorrente(String linha){
        List<NumeroNaLinha> numerosNaLinhas = new ArrayList<>();
        Matcher matcher = acharNumeros.matcher(linha);

        while (matcher.find()) {
            numerosNaLinhas.add(
                new NumeroNaLinha(
                    Integer.parseInt(matcher.group()),
                    matcher.start(),
                    matcher.end() - 1
                )
            );
        }
        return numerosNaLinhas;
    }

    public static boolean numeroPossuiSimbolosAdjacentes(List<String> linhasASerAnalisadas, NumeroNaLinha numero){
        EscopoDeAnalise escopoDeAnalise = defineEscopoDeAnaliseAdjacenteDoObjeto(numero.inicioDaPosicaoNaLinha(), numero.terminoDaPosicaoNaLinha(), false);
        
        for(String linhaCorrente : linhasASerAnalisadas){
            Matcher matcher = acharSimbolos.matcher(linhaCorrente).region(escopoDeAnalise.inicioDoEscopo(), escopoDeAnalise.fimDoEscopo());
            if(matcher.find())
                return true;
        }
        return false;
    }

    public static EscopoDeAnalise defineEscopoDeAnaliseAdjacenteDoObjeto(int inicioDoObjeto, int terminoDoObjeto, boolean escopoParaAsterisco){
        int inicioDoEscopo = 0;
        int fimDoEscopo = 0;
        int espacamento = 0;

        if (escopoParaAsterisco) {
            espacamento = 1;
        } else {
            espacamento = 2;
        }

        if (inicioDoObjeto == 0) {
            fimDoEscopo = terminoDoObjeto + espacamento;
        } else if (terminoDoObjeto == 139) {
            inicioDoEscopo = inicioDoObjeto - 1;
            fimDoEscopo = terminoDoObjeto;
        } else {
            inicioDoEscopo = inicioDoObjeto - 1;
            fimDoEscopo = terminoDoObjeto + espacamento;
        }
        return new EscopoDeAnalise(inicioDoEscopo, fimDoEscopo);
    }

    public record NumeroNaLinha(int valorDoNumero, int inicioDaPosicaoNaLinha, int terminoDaPosicaoNaLinha) {}
    public record EscopoDeAnalise(int inicioDoEscopo, int fimDoEscopo){}
}