package pl.bsf.lukasz.koc.currencyaccounts.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CURRENCY_ACCOUNTS")
public class CurrencyAccount {

	@Id
	@GeneratedValue
	private Long id;

	@Column(nullable = false, length = 50)
	private String firstName;

	@Column(nullable = false, length = 100)
	private String lastName;

	@Column(nullable = false, scale = 2)
	private BigDecimal balance;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Currency currency;
}
