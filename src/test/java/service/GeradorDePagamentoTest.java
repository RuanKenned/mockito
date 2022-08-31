package service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import br.com.alura.leilao.service.GeradorDePagamento;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class GeradorDePagamentoTest {

    private GeradorDePagamento geradorDePagamento;

    @Mock
    private PagamentoDao pagamentoDao;

    @Mock
    private Clock clock;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @BeforeEach
    public void init(){
        MockitoAnnotations.initMocks(this);
        this.geradorDePagamento = new GeradorDePagamento(pagamentoDao, clock);
    }

    @Test
    public void deveriaCriarPagamentoParaVencedorDoLeilao(){
        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2022, 8, 31);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        //Captura o argumento que foi criado na classe original e eu não tenho acesso
        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();
        assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertEquals(leilao, pagamento.getLeilao());
        assertFalse(pagamento.getPago());
    }

    @Test
    public void deveriaCriarDataVencimentoDoPagamentoParaSegundaSeForSexta(){
        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2022, 8, 26);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        //Captura o argumento que foi criado na classe original e eu não tenho acesso
        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();
        assertEquals(LocalDate.now(clock).plusDays(3), pagamento.getVencimento());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertEquals(leilao, pagamento.getLeilao());
        assertFalse(pagamento.getPago());
    }

    @Test
    public void deveriaCriarDataVencimentoDoPagamentoParaSegundaSeForSabado(){
        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2022, 8, 27);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        //Captura o argumento que foi criado na classe original e eu não tenho acesso
        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();
        assertEquals(LocalDate.now(clock).plusDays(2), pagamento.getVencimento());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertEquals(leilao, pagamento.getLeilao());
        assertFalse(pagamento.getPago());
    }

    @Test
    public void deveriaCriarDataVencimentoDoPagamentoParaSegundaSeForDoming(){
        Leilao leilao = leilao();
        Lance lanceVencedor = leilao.getLanceVencedor();

        LocalDate data = LocalDate.of(2022, 8, 28);
        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Mockito.when(clock.instant()).thenReturn(instant);
        Mockito.when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        //Captura o argumento que foi criado na classe original e eu não tenho acesso
        Mockito.verify(pagamentoDao).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();
        assertEquals(LocalDate.now(clock).plusDays(1), pagamento.getVencimento());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertEquals(leilao, pagamento.getLeilao());
        assertFalse(pagamento.getPago());
    }

    private Leilao leilao() {
        Leilao leilao = new Leilao("Celular", new BigDecimal("500"), new Usuario("Fulano"));

        Lance lance = new Lance(new Usuario("Ciclano"), new BigDecimal("900"));
        leilao.propoe(lance);
        leilao.setLanceVencedor(lance);

        return leilao;
    }
}
