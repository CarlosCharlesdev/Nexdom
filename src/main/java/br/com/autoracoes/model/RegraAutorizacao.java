package br.com.autoracoes.model;

public class RegraAutorizacao {
    private Long id;
    private String procedimentoCodigo;
    private String sexoNecessario;
    private Integer idade;
    private Boolean resultado;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getProcedimentoCodigo() { return procedimentoCodigo; }
    public void setProcedimentoCodigo(String procedimentoCodigo) { this.procedimentoCodigo = procedimentoCodigo; }
    public String getSexoNecessario() { return sexoNecessario; }
    public void setSexoNecessario(String sexoNecessario) { this.sexoNecessario = sexoNecessario; }
    public Boolean getResultado() { return resultado; }
    public void setResultado(Boolean resultado) { this.resultado = resultado; }
    public Integer getIdade() {
        return idade;
    }
    public void setIdade(Integer idade) {
        this.idade = idade;
    }
}
