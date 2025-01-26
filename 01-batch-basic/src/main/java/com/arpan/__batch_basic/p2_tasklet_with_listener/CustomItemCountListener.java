package com.arpan.__batch_basic.p2_tasklet_with_listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.scope.context.ChunkContext;

@Slf4j
public class CustomItemCountListener implements ChunkListener {
    @Override
    public void beforeChunk(ChunkContext context) {
    }

    @Override
    public void afterChunk(ChunkContext context) {
        long count = context.getStepContext().getStepExecution().getReadCount();
        log.info("ItemCount: {}", count);
    }

    @Override
    public void afterChunkError(ChunkContext context) {
    }
}
