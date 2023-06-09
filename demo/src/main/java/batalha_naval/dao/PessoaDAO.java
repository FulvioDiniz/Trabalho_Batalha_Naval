package batalha_naval.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import batalha_naval.dao.core.GenericJDBCDAO;
import batalha_naval.model.Pessoa;
import batalha_naval.model.Filter.PessoaFilter;

public class PessoaDAO extends GenericJDBCDAO<Pessoa, Long> {
    public PessoaDAO(Connection connection) {
        super(connection);
    }

    // private List<Pessoa> pessoas = new ArrayList<>();

    private static final String CREATE_QUERY = "INSERT INTO dados (nome, senha, ponto, acertos, erros) VALUES (?,?,0,0,0);";
    private static final String FIND_BY_NAME_PASSWORD_QUERY = "SELECT nome,senha FROM dados where nome = ? And senha =?;";
    private static final String FIND_ALL_QUERY = "SELECT nome, senha, ponto, acertos, erros FROM dados where nome = ?;";
    private static final String UPDATE_QUERY = "UPDATE dados SET ponto = ?, acertos = ?, erros = ? WHERE nome = ?;";
    private static final String DELETE_QUERY = "DELETE FROM dados WHERE nome = ?;";
    private static final String FIND_TOP_10 = "SELECT nome, ponto FROM dados ORDER BY ponto DESC LIMIT 10;";

    @Override
    protected Pessoa toEntity(ResultSet resultSet) throws SQLException {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(resultSet.getLong("id"));
        pessoa.setNome(resultSet.getString("nome"));
        pessoa.setSenha(resultSet.getString("senha"));
        pessoa.setPonto(resultSet.getInt("ponto"));
        pessoa.setAcertos(resultSet.getInt("acertos"));
        pessoa.setErros(resultSet.getInt("erros"));
        return pessoa;
    }

    public boolean findByNome(String nome) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY)) {
            statement.setString(1, nome);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected String createQuery() {
        return CREATE_QUERY;
    }

    @Override
    protected String updateQuery() {
        return UPDATE_QUERY;
    }

    @Override
    protected String removeQuery() {
        return DELETE_QUERY;
    }

    @Override
    protected void addParameters(PreparedStatement statement, Pessoa entity) throws SQLException {
        statement.setString(1, entity.getNome());
        statement.setString(2, entity.getSenha());
        statement.setInt(3, entity.getPonto());
        statement.setInt(4, entity.getAcertos());
        statement.setInt(5, entity.getErros());
    }

    public boolean findByNameAndPassword(String nome, String senha) {
        try {
            PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_PASSWORD_QUERY);
            statement.setString(1, nome);
            statement.setString(2, senha);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                System.out.println("Nome: " + resultSet.getString("nome"));
                System.out.println("Senha: " + resultSet.getString("senha"));
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pessoa");
        }
        return false;
    }

    public PessoaFilter findByname(String nome) {
        try {
            PreparedStatement statement = connection.prepareStatement(FIND_ALL_QUERY);
            statement.setString(1, nome);
            ResultSet resultSet = statement.executeQuery();
            PessoaFilter pessoa = new PessoaFilter();
            while (resultSet.next()) {
                pessoa.setNome(resultSet.getString("nome"));
                pessoa.setPonto(resultSet.getInt("ponto"));
                pessoa.setAcertos(resultSet.getInt("acertos"));
                pessoa.setErros(resultSet.getInt("erros"));
            }
            return pessoa;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pessoa");
        }
        return null;
    }

    public List<Pessoa> orderByPontos(){
        try{
            PreparedStatement statement = connection.prepareStatement(FIND_TOP_10);
            ResultSet resultSet = statement.executeQuery();
            List<Pessoa> pessoas = new ArrayList<Pessoa>();
            while (resultSet.next()) {
                Pessoa pessoa = new Pessoa();
                pessoa.setNome(resultSet.getString("nome"));
                pessoa.setPonto(resultSet.getInt("ponto"));
                pessoas.add(pessoa);
            }
            return pessoas;
        } catch (SQLException e) {
            System.out.println("Erro ao buscar pessoa");
        }
        return null;
    }

    public boolean cadastrarPessoa(String nome, String senha) {
        try {
            PreparedStatement statement = connection.prepareStatement(CREATE_QUERY);
            statement.setString(1, nome);
            statement.setString(2, senha);
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println("Erro ao criar pessoa");
        }
        return false;
    }

    @Override
    protected void setKeyInStatementFromEntity(PreparedStatement statement, Pessoa entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setKeyInStatementFromEntity'");
    }

    @Override
    protected void setKeyInStatement(PreparedStatement statement, Long key) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setKeyInStatement'");
    }

    @Override
    protected void setKeyInEntity(ResultSet rs, Pessoa entity) throws SQLException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setKeyInEntity'");
    }

    @Override
    protected String findByKeyQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByKeyQuery'");
    }

    @Override
    protected String findAllQuery() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findAllQuery'");
    }

}
