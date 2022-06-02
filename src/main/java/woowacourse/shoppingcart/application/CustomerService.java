package woowacourse.shoppingcart.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import woowacourse.exception.auth.EmailDuplicateException;
import woowacourse.exception.auth.PasswordIncorrectException;
import woowacourse.shoppingcart.dao.CustomerDao;
import woowacourse.shoppingcart.domain.Customer;
import woowacourse.shoppingcart.domain.Encoder;
import woowacourse.shoppingcart.exception.CustomerNotFoundException;
import woowacourse.shoppingcart.ui.dto.request.CustomerDeleteRequest;
import woowacourse.shoppingcart.ui.dto.request.CustomerRequest;
import woowacourse.shoppingcart.ui.dto.request.CustomerResponse;
import woowacourse.shoppingcart.ui.dto.request.CustomerUpdatePasswordRequest;
import woowacourse.shoppingcart.ui.dto.request.CustomerUpdateProfileRequest;

@Service
public class CustomerService {

    private final CustomerDao customerDao;
    private final Encoder encoder;

    public CustomerService(CustomerDao customerDao) {
        this.customerDao = customerDao;
        this.encoder = new PasswordEncoderAdapter();
    }

    @Transactional
    public long create(CustomerRequest customerRequest) {
        validateDuplicateEmail(customerRequest);

        final String hashPw = encoder.encode(customerRequest.getPassword());
        final Customer customer = new Customer(customerRequest.getEmail(), customerRequest.getName(), hashPw);

        return customerDao.save(customer);
    }

    private void validateDuplicateEmail(CustomerRequest customerRequest) {
        if (customerDao.findByEmail(customerRequest.getEmail()).isPresent()) {
            throw new EmailDuplicateException();
        }
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        final Customer customer = customerDao.findById(id).orElseThrow(CustomerNotFoundException::new);
        return new CustomerResponse(customer.getName(), customer.getEmail());
    }

    public Customer getIdByEmail(String email) {
        return customerDao.findByEmail(email).orElseThrow(CustomerNotFoundException::new);
    }

    @Transactional
    public long updateProfile(Long id, CustomerUpdateProfileRequest customerUpdateProfileRequest) {
        final Customer customer = customerDao.findById(id).orElseThrow(CustomerNotFoundException::new);

        customerDao.updateProfile(customer.changeName(customerUpdateProfileRequest.getName()));
        return id;
    }

    @Transactional
    public long updatePassword(Long id, CustomerUpdatePasswordRequest customerUpdatePasswordRequest) {
        final Customer customer = customerDao.findById(id).orElseThrow(CustomerNotFoundException::new);
        validatePassword(customer, customerUpdatePasswordRequest.getOldPassword());

        customerDao.updatePassword(
                customer.changePassword(encoder.encode(customerUpdatePasswordRequest.getNewPassword())));
        return id;
    }

    private void validatePassword(Customer customer, String inputPassword) {
        if (!customer.validatePassword(inputPassword, encoder)) {
            throw new PasswordIncorrectException();
        }
    }

    public long delete(long id, CustomerDeleteRequest customerDeleteRequest) {
        final Customer customer = customerDao.findById(id).orElseThrow(CustomerNotFoundException::new);
        validatePassword(customer, customerDeleteRequest.getPassword());

        customerDao.delete(id);
        return id;
    }
}
