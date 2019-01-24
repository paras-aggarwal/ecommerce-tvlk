package paymentmicroservice.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentmicroservice.Models.CheckOut;
import paymentmicroservice.Models.Order;
import paymentmicroservice.Models.PaymentOptionSelected;
import paymentmicroservice.Models.Response;
import paymentmicroservice.Service.BankAvailableService;
import paymentmicroservice.Service.PaymentOptionService;


@RestController
public class PaymentController {

    @Autowired
    PaymentOptionService paymentOptionService;
    @Autowired
    BankAvailableService bankAvailableService;
    @RequestMapping(method = RequestMethod.POST,value = "/checkout")
    public ResponseEntity<Response> checkOut(@RequestBody CheckOut checkOut)
    {
        return paymentOptionService.getOptions(checkOut);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/payment")
    public ResponseEntity<Response> payment(@RequestBody PaymentOptionSelected paymentOptionSelected)
    {
        return paymentOptionService.validate(paymentOptionSelected);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/payment/netbanking")
    public ResponseEntity<Response> netBanking(@RequestBody Order order)
    {
       return bankAvailableService.getBanks(order);
    }








}
