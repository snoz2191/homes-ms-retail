package com.tenx.ms.retail.store.services;

import com.tenx.ms.retail.store.domain.StoreEntity;
import com.tenx.ms.retail.store.repository.StoreRepository;
import com.tenx.ms.retail.store.rest.dto.Store;
import com.tenx.ms.retail.store.function.StoreConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreService {
    private static final Logger LOGGER = LoggerFactory.getLogger(StoreService.class);

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private StoreConverter converter;

    @Transactional
    public Long createStore(Store store) {
        StoreEntity storeEntity = converter.convertToStoreEntity.apply(store);
        LOGGER.debug("Save store {} into database", storeEntity);
        storeRepository.save(storeEntity);
        return storeEntity.getStoreId();
    }

    public Optional<Store> getStoreById(Long listingId) {
        return storeRepository.findOneByStoreId(listingId).map(converter.convertToStoreDTO);
    }

    public List<Store> getAllStores(Pageable pageable) {
        return storeRepository.findAll(pageable).getContent().stream().map(converter.convertToStoreDTO).collect(Collectors.toList());
    }
}
