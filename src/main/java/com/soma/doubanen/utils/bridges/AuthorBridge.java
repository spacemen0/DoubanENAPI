package com.soma.doubanen.utils.bridges;

import com.soma.doubanen.domains.entities.AuthorEntity;
import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;

public class AuthorBridge implements ValueBridge<AuthorEntity, String> {

  //    @Override
  //    public AuthorEntity fromIndexedValue(String value, ValueBridgeFromIndexedValueContext
  // context) {
  //        Optional<AuthorEntity> authorEntity = authorRepository.findByName(value);
  ////        return ValueBridge.super.fromIndexedValue(value, context);
  //        return authorEntity.orElse(null);
  //    }

  @Override
  public String toIndexedValue(
      AuthorEntity authorEntity,
      ValueBridgeToIndexedValueContext valueBridgeToIndexedValueContext) {
    return authorEntity.getName();
  }
}
