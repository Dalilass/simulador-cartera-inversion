package com.simulador.cartera.config;

import com.simulador.cartera.entity.Activo;
import com.simulador.cartera.entity.Operacion;
import com.simulador.cartera.enums.TipoActivo;
import com.simulador.cartera.enums.TipoOperacion;
import com.simulador.cartera.repository.ActivoRepository;
import com.simulador.cartera.repository.OperacionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Carga datos ficticios al arrancar la aplicación.
 * NOTA: Todos los activos, precios y operaciones son inventados
 * con fines educativos. No representan datos reales del mercado.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final ActivoRepository activoRepository;
    private final OperacionRepository operacionRepository;

    @Override
    public void run(String... args) {
        log.info("Cargando datos ficticios de ejemplo...");

        // === ACTIVOS FICTICIOS ===
        Activo msft = crearActivo("MSFT", "MegaSoft Corp", TipoActivo.ACCION,
                "Tecnología", "EUR", "342.50");

        Activo amzn = crearActivo("AMZN", "AmazonRiver Inc", TipoActivo.ACCION,
                "Comercio electrónico", "EUR", "178.20");

        Activo spy = crearActivo("VWCE", "Vanguard World ETF", TipoActivo.ETF,
                "Mercado global", "EUR", "104.30");

        Activo btc = crearActivo("BSIM", "Bitcoin Simulator", TipoActivo.CRYPTO,
                "Criptomoneda", "EUR", "61200.00");

        Activo ete = crearActivo("ETFSIM", "ETF Simulador Europa", TipoActivo.ETF,
                "Mercado europeo", "EUR", "55.80");

        Activo tele = crearActivo("TELE5", "Telecom Simulada SA", TipoActivo.ACCION,
                "Telecomunicaciones", "EUR", "4.12");

        Activo bond = crearActivo("BONO10", "Bono Estado Simulado 10A", TipoActivo.BONO,
                "Renta fija", "EUR", "98.50");

        Activo fondo = crearActivo("FNDTEC", "Fondo Tecnología Simulado", TipoActivo.FONDO,
                "Tecnología", "EUR", "23.45");

        activoRepository.saveAll(List.of(msft, amzn, spy, btc, ete, tele, bond, fondo));
        log.info("✓ {} activos ficticios creados", activoRepository.count());

        // === OPERACIONES FICTICIAS ===
        // Compras de MSFT
        registrarOperacion(msft, TipoOperacion.COMPRA, LocalDate.of(2023, 3, 15),
                "10", "285.00");
        registrarOperacion(msft, TipoOperacion.COMPRA, LocalDate.of(2023, 9, 20),
                "5", "310.00");
        registrarOperacion(msft, TipoOperacion.DIVIDENDO, LocalDate.of(2024, 1, 10),
                "15", "0.75"); // 15 acciones x 0.75€ dividendo

        // Compras de AMZN
        registrarOperacion(amzn, TipoOperacion.COMPRA, LocalDate.of(2023, 5, 10),
                "20", "135.50");
        registrarOperacion(amzn, TipoOperacion.COMPRA, LocalDate.of(2024, 1, 5),
                "10", "155.00");

        // ETF VWCE
        registrarOperacion(spy, TipoOperacion.COMPRA, LocalDate.of(2022, 11, 1),
                "30", "88.00");
        registrarOperacion(spy, TipoOperacion.COMPRA, LocalDate.of(2023, 6, 1),
                "20", "96.50");
        registrarOperacion(spy, TipoOperacion.DIVIDENDO, LocalDate.of(2023, 12, 15),
                "50", "0.42");

        // Bitcoin Simulator - compra y venta parcial
        registrarOperacion(btc, TipoOperacion.COMPRA, LocalDate.of(2023, 1, 20),
                "0.15", "22500.00");
        registrarOperacion(btc, TipoOperacion.COMPRA, LocalDate.of(2023, 10, 5),
                "0.05", "27000.00");
        registrarOperacion(btc, TipoOperacion.VENTA, LocalDate.of(2024, 3, 1),
                "0.05", "58000.00"); // Venta parcial con beneficio

        // ETF Europa
        registrarOperacion(ete, TipoOperacion.COMPRA, LocalDate.of(2023, 4, 12),
                "50", "48.20");

        // Telecom
        registrarOperacion(tele, TipoOperacion.COMPRA, LocalDate.of(2022, 8, 15),
                "200", "3.80");
        registrarOperacion(tele, TipoOperacion.DIVIDENDO, LocalDate.of(2023, 6, 30),
                "200", "0.20");
        registrarOperacion(tele, TipoOperacion.DIVIDENDO, LocalDate.of(2024, 6, 28),
                "200", "0.22");

        // Bono
        registrarOperacion(bond, TipoOperacion.COMPRA, LocalDate.of(2023, 2, 1),
                "10", "100.00");

        // Fondo tecnología
        registrarOperacion(fondo, TipoOperacion.COMPRA, LocalDate.of(2023, 7, 15),
                "100", "20.50");
        registrarOperacion(fondo, TipoOperacion.COMPRA, LocalDate.of(2024, 1, 20),
                "50", "22.10");

        log.info("✓ {} operaciones ficticias creadas", operacionRepository.count());
        log.info("Datos de ejemplo cargados correctamente. ¡La aplicación está lista!");
    }

    private Activo crearActivo(String ticker, String nombre, TipoActivo tipo,
                                String sector, String moneda, String precio) {
        return Activo.builder()
                .ticker(ticker)
                .nombre(nombre)
                .tipo(tipo)
                .sector(sector)
                .moneda(moneda)
                .precioActual(new BigDecimal(precio))
                .build();
    }

    private void registrarOperacion(Activo activo, TipoOperacion tipo,
                                     LocalDate fecha, String cantidad, String precio) {
        BigDecimal cant = new BigDecimal(cantidad);
        BigDecimal prec = new BigDecimal(precio);
        Operacion op = Operacion.builder()
                .activo(activo)
                .tipoOperacion(tipo)
                .fecha(fecha)
                .cantidad(cant)
                .precioUnitario(prec)
                .importeTotal(cant.multiply(prec))
                .build();
        operacionRepository.save(op);
    }
}
