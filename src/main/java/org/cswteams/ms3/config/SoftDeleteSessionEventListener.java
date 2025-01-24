package org.cswteams.ms3.config;

import org.cswteams.ms3.config.annotations.Param;
import org.cswteams.ms3.config.annotations.SoftDeletable;
import org.hibernate.event.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class SoftDeleteSessionEventListener implements /*PreLoadEventListener,*/ PostLoadEventListener, PreUpdateEventListener  {
    Logger logger = LoggerFactory.getLogger(SoftDeleteSessionEventListener.class);

    @Autowired
    private SoftDeleteService softDeleteService;

//    @Override
    public void onPreLoad(PreLoadEvent event) {
        // Quando un'entità viene aggiornata, verifica se è annotata con @SoftDeletable
        if (event.getEntity() == null) return;

        Object entity = event.getEntity();

        if (!entity.getClass().isAnnotationPresent(SoftDeletable.class)) return;

        SoftDeletable annotation = entity.getClass().getAnnotation(SoftDeletable.class);

        enableSoftDeleteFilter(annotation);
    }

    @Override
    public void onPostLoad(PostLoadEvent event) {
        // Quando un'entità viene aggiornata, verifica se è annotata con @SoftDeletable
        if (event.getEntity() == null) return;

        Object entity = event.getEntity();

        if (!entity.getClass().isAnnotationPresent(SoftDeletable.class)) return;

        SoftDeletable annotation = entity.getClass().getAnnotation(SoftDeletable.class);

        enableSoftDeleteFilter(annotation);
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        // Quando un'entità viene aggiornata, verifica se è annotata con @SoftDeletable
        if (event.getEntity() == null) return false;

        Object entity = event.getEntity();

        if (!entity.getClass().isAnnotationPresent(SoftDeletable.class)) return false;

        SoftDeletable annotation = entity.getClass().getAnnotation(SoftDeletable.class);

        enableSoftDeleteFilter(annotation);

        return true;
    }

    private void enableSoftDeleteFilter(SoftDeletable annotation) {
        if (softDeleteService == null) return;

        String filterName = annotation.filterName();

        // Inizializza una mappa per i parametri del filtro
        Map<String, Object> filterParams = new HashMap<>();

        // Recupera i parametri definiti nell'annotazione, come "deleted"
        for (Param param : annotation.params()) {
            // Aggiungi i parametri alla mappa
            String key = param.key();
            boolean value = param.value(); // Il valore è ora un booleano

            // Aggiungi il parametro alla mappa
            filterParams.put(key, value);
        }

        // Abilita il filtro di soft delete per questa entità
        softDeleteService.enableSoftDeleteFilter(filterName, filterParams);
    }
}
