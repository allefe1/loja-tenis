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
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "Marca não pode estar em branco")
    @Size(min = 1, max = 50, message = "Marca deve ter entre 1 e 50 caracteres")
    @Column(nullable = false)
    private String marca;

    @NotNull(message = "Preço não pode estar vazio")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @NotBlank(message = "Cor não pode estar em branco")
    @Size(min = 1, max = 30, message = "Cor deve ter entre 1 e 30 caracteres")
    @Column(nullable = false)
    private String cor;

    @NotNull(message = "Tamanho não pode estar vazio")
    @Min(value = 20, message = "Tamanho mínimo é 20")
    @Max(value = 50, message = "Tamanho máximo é 50")
    @Column(nullable = false)
    private Integer tamanho;

    @NotBlank(message = "Descrição não pode estar em branco")
    @Size(min = 5, max = 500, message = "Descrição deve ter entre 5 e 500 caracteres")
    @Column(nullable = false, length = 500)
    private String descricao;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_deleted")
    @Temporal(TemporalType.TIMESTAMP)
    private Date isDeleted;

    
    public Sapato() {}

    public Sapato(String nome, String marca, BigDecimal preco, String cor, Integer tamanho, String descricao) {
        this.nome = nome;
        this.marca = marca;
        this.preco = preco;
        this.cor = cor;
        this.tamanho = tamanho;
        this.descricao = descricao;
    }

    
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

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Date getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Date isDeleted) { this.isDeleted = isDeleted; }

    @Override
    public String toString() {
        return "Sapato{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", marca='" + marca + '\'' +
                ", preco=" + preco +
                ", cor='" + cor + '\'' +
                ", tamanho=" + tamanho +
                ", descricao='" + descricao + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isDeleted=" + isDeleted +
                '}';
    }
}
