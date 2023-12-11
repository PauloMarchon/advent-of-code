import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    static final Pattern patternNumeroCor = Pattern.compile("(\\d+)\\s+([^,;]+)");
    public static void main(String[] args) throws IOException {
        List<String> input = Files.readAllLines(Path.of("input.txt"));

        int soma =  input.stream()
                    .mapToInt(Main::buscaValoresDeCadaCorPorGame)
                    .sum();
        System.out.println(soma);

        int poder = input.stream()
                    .mapToInt(Main::poderDoConjunto)
                    .sum();
        System.out.println(poder);
    }

    public static int buscaValoresDeCadaCorPorGame(String linha) {
        Matcher matcher = patternNumeroCor.matcher(linha);
        int gameId = Integer.parseInt(linha.split(": ")[0].replace("Game ", ""));

        while (matcher.find()) {
            String cor = matcher.group(2).trim().toUpperCase();
            int quantidade = Integer.parseInt(matcher.group(1));

            if (quantidadeDeCubosUltrapassaOLimite(Cor.valueOf(cor), quantidade))
                return 0;
        }
        return gameId;
    }

    public static boolean quantidadeDeCubosUltrapassaOLimite(Cor cor, int resultado) {
        switch (cor) {
            case RED -> {
                return resultado > Cor.RED.maximo;
            }
            case BLUE -> {
                return resultado > Cor.BLUE.maximo;
            }
            case GREEN -> {
                return resultado > Cor.GREEN.maximo;
            }
            default -> {
                return false;
            }
        }
    }

    public static int poderDoConjunto(String linha) {
        int red = acharQuantidadeMinimaDeOcorrencias(Cor.RED, linha);
        int blue = acharQuantidadeMinimaDeOcorrencias(Cor.BLUE, linha);
        int green = acharQuantidadeMinimaDeOcorrencias(Cor.GREEN, linha);

        return red * blue * green;
    }

   public static int acharQuantidadeMinimaDeOcorrencias(Cor cor, String linha) {
       Matcher matcher = patternNumeroCor.matcher(linha);
       int minimo = 0;

       while (matcher.find()) {
           String corAtual = matcher.group(2).trim().toUpperCase();
           int quantidade = Integer.parseInt(matcher.group(1));

           if (corAtual.equals(cor.toString()) && minimo < quantidade)
               minimo = quantidade;
       }
       return minimo;
   }

    public enum Cor {
        RED(12),
        GREEN(13),
        BLUE(14);
        private final int maximo;
        Cor(final int maximo) {
            this.maximo = maximo;
        }

        public int getMaximo() {
            return maximo;
        }
    }
}