package service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.EnviadorDeEmails;
import br.com.alura.leilao.service.FinalizarLeilaoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FinalizarLeilaoServiceTest {
    private FinalizarLeilaoService service;

    @Mock
    private LeilaoDao leilaoDao;

    @Mock
    private EnviadorDeEmails enviadorDeEmails;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this); // Cria todos os mocks dos atributos marcados com @Mock
        this.service = new FinalizarLeilaoService(leilaoDao, enviadorDeEmails);
    }

    @Test
    public void deveriaFinalizarUmLeilao(){
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes); //Por ser um duble, leilaoDao.buscarLeiloesExpirados() retorna uma lista vazia, então eu insiro um retorno para teste

        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);

        assertTrue(leilao.isFechado());
        assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());
        Mockito.verify(leilaoDao).salvar(leilao); // verifica se o método salvar foi chamado
    }

    @Test
    public void deveriaEnviarEmailParaVencedorDoLeilao(){
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes); //Por ser um duble, leilaoDao.buscarLeiloesExpirados() retorna uma lista vazia, então eu insiro um retorno para teste

        service.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);
        Lance lanceVencedor = leilao.getLanceVencedor();

        Mockito.verify(enviadorDeEmails).enviarEmailVencedorLeilao(lanceVencedor); // verifica se o método enviarEmailVencedorLeilao foi chamado
    }

    @Test
    public void naoDeveriaEnviarEmailParaVencedorDoLeilaoEmCasoDeErroAoEncerrarOLeilao(){
        List<Leilao> leiloes = leiloes();
        Mockito.when(leilaoDao.buscarLeiloesExpirados()).thenReturn(leiloes); //Por ser um duble, leilaoDao.buscarLeiloesExpirados() retorna uma lista vazia, então eu insiro um retorno para teste

        Mockito.when(leilaoDao.salvar(Mockito.any())).thenThrow(RuntimeException.class);

        try {
            service.finalizarLeiloesExpirados();
            Mockito.verifyNoInteractions(enviadorDeEmails);
        }catch (Exception e){}
    }

    // Trecho de código omitido
    private List<Leilao> leiloes() {
        List<Leilao> lista = new ArrayList<>();

        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

        Lance primeiro = new Lance(new Usuario("Beltrano"), new BigDecimal("600"));
        Lance segundo = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));

        leilao.propoe(primeiro);
        leilao.propoe(segundo);

        lista.add(leilao);

        return lista;
    }
}
