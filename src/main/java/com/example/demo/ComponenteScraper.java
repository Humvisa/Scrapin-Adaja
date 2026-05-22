package com.example.demo;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Component
public class ComponenteScraper {

    @Autowired
    private ResultadoRepository repository;

    // 🤖 Se ejecuta automáticamente cada hora (3600000 ms)
    // Nota: Si quieres probarlo rápido en bucle puedes cambiarlo temporalmente a (fixedRate = 10000)
    @Scheduled(fixedRate = 3600000)
    public void ejecutarScrapingAutomatico() {
        try {
            System.out.println("🤖 Iniciando raspado automático del río Adaja (SAIH Duero)...");

            // 1. Conexión y lectura del dato actual en la ficha de la estación EA046
            String urlWeb = "https://www.saihduero.es/risr/EA046";
            Document doc = Jsoup.connect(urlWeb)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                    .timeout(15000)
                    .get();

            // Selector objetivo: Busca el valor dentro del panel de Caudal
            String textoCaudal = doc.select("div.panel:contains(Caudal) div.panel-body, td:contains(Caudal) + td").text();
            if (textoCaudal == null || textoCaudal.trim().isEmpty()) {
                textoCaudal = doc.select(".panel-body .texto-grande, .widget-subheading:contains(Caudal) + .widget-numbers").text();
            }

            // 2. Limpieza estricta para evitar errores de formato o múltiples lecturas
            textoCaudal = textoCaudal.trim();
            String[] partes = textoCaudal.split("\\s+");
            String primerBloque = partes[0];

            primerBloque = primerBloque.replace(",", ".");
            String numeroLimpio = primerBloque.replaceAll("[^0-9.]", "");

            double caudalReal = Double.parseDouble(numeroLimpio);
            System.out.println("🎯 Caudal real procesado con éxito: " + caudalReal + " m³/s");

            // 3. Guardar el nuevo registro en Supabase (Irlanda)
            ResultadoScraper nuevoRegistro = new ResultadoScraper();
            nuevoRegistro.setNombreRio("Río Adaja (Estación EA046)");
            nuevoRegistro.setCaudal(caudalReal);
            nuevoRegistro.setFecha(LocalDate.now());
            nuevoRegistro.setHora(LocalTime.now());

            repository.save(nuevoRegistro);
            System.out.println("✅ Nuevo registro guardado en Supabase.");

            // 4. 🧹 BÚFER CIRCULAR: Mantener estrictamente una ventana flotante de 24 registros
            List<ResultadoScraper> todos = repository.findAll();

            if (todos != null && todos.size() > 24) {
                // Ordenamos la lista por ID de menor a mayor (los primeros elementos serán siempre los más antiguos)
                todos.sort((a, b) -> Long.compare(a.getId(), b.getId()));

                // Calculamos cuántos registros sobran del límite de 24 horas
                int registrosAExceder = todos.size() - 24;
                System.out.println("🧹 Límite de 24 horas superado. Eliminando historial antiguo redundante...");

                // Borramos de Supabase los registros más antiguos (los primeros del array ordenado)
                for (int i = 0; i < registrosAExceder; i++) {
                    ResultadoScraper registroViejo = todos.get(i);
                    repository.delete(registroViejo);
                    System.out.println("🗑️ Registro antiguo eliminado con éxito (ID: " + registroViejo.getId() + " - Hora: " + registroViejo.getHora() + ")");
                }
            }

        } catch (Exception e) {
            System.out.println("❌ Error crítico en el ciclo del scraper del SAIH: " + e.getMessage());
            e.printStackTrace();
        }
    }
}