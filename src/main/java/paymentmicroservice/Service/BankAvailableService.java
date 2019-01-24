package paymentmicroservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import paymentmicroservice.Models.Order;
import paymentmicroservice.Models.PaymentOptionSelected;
import paymentmicroservice.Models.Response;
import paymentmicroservice.Repository.BankAvailableRepo;

@Service
public class BankAvailableService {

    @Autowired
    BankAvailableRepo bankAvailableRepo;
    public ResponseEntity<Response> getBanks(Order order)
    {
        Response response=new Response();
        response.options=bankAvailableRepo.getAllBankS();
        response.orderId =order.orderId;
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
