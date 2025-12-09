package br.com.alura.literalura.principal;

import br.com.alura.literalura.model.Autor;
import br.com.alura.literalura.model.DadosGutendex;
import br.com.alura.literalura.model.Livro;
import br.com.alura.literalura.repository.AutorRepository;
import br.com.alura.literalura.repository.LivroRepository;
import br.com.alura.literalura.service.ConsumoApi;
import br.com.alura.literalura.service.ConverteDados;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Scanner;

public class Principal {

    private Scanner leitura = new Scanner(System.in);
    private ConsumoApi consumo = new ConsumoApi();
    private ConverteDados conversor = new ConverteDados();
    private final String ENDERECO = "https://gutendex.com/books/?search=";

    private LivroRepository repositorio;
    private AutorRepository autorRepository;

    public Principal(LivroRepository repositorio, AutorRepository autorRepository) {
        this.repositorio = repositorio;
        this.autorRepository = autorRepository;
    }

    public void exibeMenu() {
        var opcao = -1;
        while (opcao != 0) {
            var menu = """
                    
                    --------------------------------
                    Escolha o número de sua opção:
                    1 - Buscar livro pelo título (API)
                    2 - Listar livros registrados (Banco)
                    3 - Listar autores registrados (Banco)
                    4 - Listar autores vivos em um determinado ano
                    5 - Listar livros em um determinado idioma
                    6 - Top 10 livros mais baixados
                    7 - Buscar autor pelo nome (Banco)
                    8 - Exibir média de downloads dos livros (Estatísticas)
                    0 - Sair
                    --------------------------------
                    """;

            System.out.println(menu);
            opcao = leitura.nextInt();
            leitura.nextLine();

            switch (opcao) {
                case 1:
                    buscarLivroWeb();
                    break;
                case 2:
                    listarLivrosRegistrados();
                    break;
                case 3:
                    listarAutoresRegistrados();
                    break;
                case 4:
                    listarAutoresVivos();
                    break;
                case 5:
                    listarLivrosPorIdioma();
                    break;
                case 6:
                    listarTop10Livros();
                    break;
                case 7:
                    buscarAutorPorNome();
                    break;
                case 8:
                    exibirEstatisticas();
                    break;
                case 0:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida");
            }
        }
    }

    private void buscarLivroWeb() {
        System.out.println("Digite o nome do livro para busca:");
        var nomeLivro = leitura.nextLine();
        var json = consumo.obterDados(ENDERECO + nomeLivro.replace(" ", "%20"));
        DadosGutendex dados = conversor.obterDados(json, DadosGutendex.class);

        if (dados.resultados().isEmpty()) {
            System.out.println("Nenhum livro encontrado");
            return;
        }

        var dadosLivro = dados.resultados().get(0);
        Livro livro = new Livro(dadosLivro);

        var dadosAutor = dadosLivro.autores().get(0);
        Autor autor = autorRepository.findByNome(dadosAutor.nome());

        if (autor == null) {
            autor = new Autor(dadosAutor);
            autorRepository.save(autor);
        }

        livro.setAutor(autor);

        try {
            repositorio.save(livro);
            System.out.println("Livro salvo com sucesso!");
            System.out.println(livro);
        } catch (Exception e) {
            System.out.println("Erro: Livro já cadastrado no banco de dados.");
        }
    }

    private void listarLivrosRegistrados() {
        List<Livro> livros = repositorio.findAll();
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro cadastrado.");
        } else {
            livros.forEach(System.out::println);
        }
    }

    private void listarAutoresRegistrados() {
        List<Autor> autores = autorRepository.findAll();
        if (autores.isEmpty()) {
            System.out.println("Nenhum autor cadastrado.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarAutoresVivos() {
        System.out.println("Insira o ano que deseja pesquisar:");
        var ano = leitura.nextInt();
        leitura.nextLine();

        List<Autor> autores = autorRepository.autoresVivosNoAno(ano);
        if (autores.isEmpty()){
            System.out.println("Nenhum autor vivo encontrado neste ano.");
        } else {
            autores.forEach(System.out::println);
        }
    }

    private void listarLivrosPorIdioma() {
        System.out.println("""
                Insira o idioma para realizar a busca:
                es - espanhol
                en - inglês
                fr - francês
                pt - português
                """);
        var idioma = leitura.nextLine();
        List<Livro> livros = repositorio.findByIdioma(idioma);
        if (livros.isEmpty()) {
            System.out.println("Nenhum livro encontrado nesse idioma.");
        } else {
            livros.forEach(System.out::println);
        }
    }

    // --- MÉTODOS NOVOS (OPCIONAIS) ---

    private void listarTop10Livros() {
        List<Livro> livros = repositorio.findTop10ByOrderByNumeroDownloadsDesc();
        System.out.println("--- TOP 10 LIVROS MAIS BAIXADOS ---");
        livros.forEach(l ->
                System.out.println(l.getTitulo() + " - Downloads: " + l.getNumeroDownloads()));
    }

    private void buscarAutorPorNome() {
        System.out.println("Digite o nome do autor que deseja buscar:");
        var nome = leitura.nextLine();
        List<Autor> autores = autorRepository.findByNomeContainingIgnoreCase(nome);

        if (autores.isEmpty()) {
            System.out.println("Nenhum autor encontrado com esse nome.");
        } else {
            System.out.println("--- Autores Encontrados ---");
            autores.forEach(System.out::println);
        }
    }

    private void exibirEstatisticas() {
        List<Livro> livros = repositorio.findAll();
        DoubleSummaryStatistics stats = livros.stream()
                .mapToDouble(Livro::getNumeroDownloads)
                .summaryStatistics();

        System.out.println("--- ESTATÍSTICAS DE DOWNLOADS ---");
        System.out.println("Média de downloads: " + stats.getAverage());
        System.out.println("Maior número de downloads: " + stats.getMax());
        System.out.println("Menor número de downloads: " + stats.getMin());
        System.out.println("Total de registros analisados: " + stats.getCount());
    }
}