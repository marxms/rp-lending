/*
 * Copyright 2025 marxmenezes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.rp.repository;

import com.rp.repository.domain.Wallet;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;

import java.util.List;
import java.util.Optional;

/**
 *
 * @author marxmenezes
 */
@Repository
@RequiredArgsConstructor
public abstract class WalletRepository implements CrudRepository<Wallet, Long> {

   @PersistenceContext
   private final EntityManager entityManager;

   public abstract Optional<Wallet> findByKey(String walletKey);



   @SuppressWarnings("unchecked")
   @Transactional(Transactional.TxType.NOT_SUPPORTED)
   public List<Wallet> getWalletHistory(String walletKey, int page, int size) {
      AuditReader auditReader = AuditReaderFactory.get(entityManager);
      return auditReader.createQuery()
              .forRevisionsOfEntity(Wallet.class, true, true)
              .add(AuditEntity.property("key").eq(walletKey))
              .addOrder(AuditEntity.revisionNumber().desc())
              .setFirstResult(page * size)
              .setMaxResults(size)
              .getResultList();
   }
}
