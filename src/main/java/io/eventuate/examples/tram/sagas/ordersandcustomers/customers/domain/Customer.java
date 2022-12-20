package io.eventuate.examples.tram.sagas.ordersandcustomers.customers.domain;


import io.eventuate.examples.tram.sagas.ordersandcustomers.commondomain.Money;

import javax.persistence.*;
import java.util.Collections;
import java.util.Map;

@Entity
@Table(name="Customer")
@Access(AccessType.FIELD)
public class Customer {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String name;

  @Embedded
  private Money creditLimit;

  @ElementCollection
  private Map<Long, Money> creditReservations;

  @Version
  private Long version;

  Money availableCredit() {
    return creditLimit.subtract(creditReservations.values().stream().reduce(Money.ZERO, Money::add));
  }

  public Customer() {
  }

  public Customer(String name, Money creditLimit) {
    this.name = name;
    this.creditLimit = creditLimit;
    this.creditReservations = Collections.emptyMap();
  }

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Money getCreditLimit() {
    return creditLimit;
  }

  public void setCreditLimit(Money creditLimit) {
    this.creditLimit = creditLimit;
  }

  public Map<Long, Money> getCreditReservations() {
    return creditReservations;
  }

  public void setCreditReservations(Map<Long, Money> creditReservations) {
    this.creditReservations = creditReservations;
  }

  public Long getVersion() {
    return version;
  }

  public void setVersion(Long version) {
    this.version = version;
  }

  public void reserveCredit(Long orderId, Money orderTotal) {
    if (availableCredit().isGreaterThanOrEqual(orderTotal)) {
      creditReservations.put(orderId, orderTotal);
    } else
      throw new CustomerCreditLimitExceededException();
  }
}
