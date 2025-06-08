package br.ufrn.lojasapatos.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "sapato")
public class Sapato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome não pode estar em branco")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nome;

    @NotBlank(message = "Marca não pode estar em branco")
    @Size(min = 2, max = 50, message = "Marca deve ter entre 2 e 50 caracteres")
    private String marca;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 6, fraction = 2, message = "Preço inválido")
    private BigDecimal preco;

    @NotBlank(message = "Cor não pode estar em branco")
    @Size(min = 3, max = 30, message = "Cor deve ter entre 3 e 30 caracteres")
    private String cor;

    @NotNull(message = "Tamanho é obrigatório")
    @Min(value = 30, message = "Tamanho mínimo é 30")
    @Max(value = 50, message = "Tamanho máximo é 50")
    private Integer tamanho;

    @NotBlank(message = "Descrição não pode estar em branco")
    @Size(min = 10, max = 500, message = "Descrição deve ter entre 10 e 500 caracteres")
    private String descricao;

    @Temporal(TemporalType.TIMESTAMP)
    private Date isDeleted;


    @NotBlank(message = "URL da imagem é obrigatória")
    private String imageUrl;

    // Construtores
    public Sapato() {}

    public Sapato(String nome, String marca, BigDecimal preco, String cor,
                  Integer tamanho, String descricao, String imageUrl) {
        this.nome = nome;
        this.marca = marca;
        this.preco = preco;
        this.cor = cor;
        this.tamanho = tamanho;
        this.descricao = descricao;
        this.imageUrl = imageUrl;
    }

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }

    public String getCor() { return cor; }
    public void setCor(String cor) { this.cor = cor; }

    public Integer getTamanho() { return tamanho; }
    public void setTamanho(Integer tamanho) { this.tamanho = tamanho; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Date getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Date isDeleted) { this.isDeleted = isDeleted; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
