package br.com.alura.literalura.model;

import jakarta.persistence.*;

@Entity
@Table(name = "livros")
public class Livro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String titulo;

    private String idioma;
    private Double numeroDownloads;

    @ManyToOne
    private Autor autor;

    public Livro(){}

    public Livro(DadosLivro dadosLivro){
        this.titulo = dadosLivro.titulo();
        this.idioma = dadosLivro.idiomas().get(0); // Pega o primeiro idioma da lista
        this.numeroDownloads = dadosLivro.numeroDownloads();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    public Double getNumeroDownloads() {
        return numeroDownloads;
    }

    public void setNumeroDownloads(Double numeroDownloads) {
        this.numeroDownloads = numeroDownloads;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    @Override
    public String toString() {
        return  "----- LIVRO -----" +
                "\nTítulo: " + titulo +
                "\nAutor: " + autor.getNome() +
                "\nIdioma: " + idioma +
                "\nDownloads: " + numeroDownloads +
                "\n-----------------";
    }
}