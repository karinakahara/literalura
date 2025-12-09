package br.com.alura.literalura.repository;

import br.com.alura.literalura.model.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LivroRepository extends JpaRepository<Livro, Long> {
    List<Livro> findByIdioma(String idioma);

    // Traz os 10 livros com mais downloads, em ordem decrescente
    List<Livro> findTop10ByOrderByNumeroDownloadsDesc();
}