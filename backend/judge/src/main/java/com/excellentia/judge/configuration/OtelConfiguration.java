package com.excellentia.judge.configuration;

import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.otel.bridge.*;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.zipkin.ZipkinSpanExporterBuilder;
import io.opentelemetry.extension.trace.propagation.B3Propagator;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SpanExporter;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.urlconnection.URLConnectionSender;

import java.util.Collections;

import static io.opentelemetry.sdk.trace.samplers.Sampler.alwaysOn;

@ConfigurationProperties(prefix = "otel")
@Configuration
public class OtelConfiguration
{
    private String url;
    private Tracer tracer;

    @PostConstruct
    public void init()
    {
        SpanExporter spanExporter = new ZipkinSpanExporterBuilder()
                .setSender(URLConnectionSender.create(url+"/api/v2/spans")).build();

// [OTel component] SdkTracerProvider is a SDK implementation for TracerProvider
        SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder().setSampler(alwaysOn())
                .addSpanProcessor(BatchSpanProcessor.builder(spanExporter).build()).build();

// [OTel component] The SDK implementation of OpenTelemetry
        OpenTelemetrySdk openTelemetrySdk = OpenTelemetrySdk.builder().setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(B3Propagator.injectingSingleHeader())).build();

// [OTel component] Tracer is a component that handles the life-cycle of a span
        io.opentelemetry.api.trace.Tracer otelTracer = openTelemetrySdk.getTracerProvider()
                .get("io.micrometer.micrometer-tracing");

// [Micrometer Tracing component] A Micrometer Tracing wrapper for OTel
        OtelCurrentTraceContext otelCurrentTraceContext = new OtelCurrentTraceContext();

// [Micrometer Tracing component] A Micrometer Tracing listener for setting up MDC
        Slf4JEventListener slf4JEventListener = new Slf4JEventListener();

// [Micrometer Tracing component] A Micrometer Tracing listener for setting
// Baggage in MDC. Customizable
// with correlation fields (currently we're setting empty list)
        Slf4JBaggageEventListener slf4JBaggageEventListener = new Slf4JBaggageEventListener(Collections.emptyList());

// [Micrometer Tracing component] A Micrometer Tracing wrapper for OTel's Tracer.
// You can consider
// customizing the baggage manager with correlation and remote fields (currently
// we're setting empty lists)
        tracer= new OtelTracer(otelTracer, otelCurrentTraceContext, event -> {
            slf4JEventListener.onEvent(event);
            slf4JBaggageEventListener.onEvent(event);
        }, new OtelBaggageManager(otelCurrentTraceContext, Collections.emptyList(), Collections.emptyList()));
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Tracer getTracer()
    {
        return tracer;
    }
}
