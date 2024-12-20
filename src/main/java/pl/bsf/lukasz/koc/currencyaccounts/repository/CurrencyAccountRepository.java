package pl.bsf.lukasz.koc.currencyaccounts.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.bsf.lukasz.koc.currencyaccounts.model.CurrencyAccount;

@Repository
public interface CurrencyAccountRepository extends JpaRepository<CurrencyAccount, Long> {

}
