package paymentmicroservice.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import paymentmicroservice.Models.*;
import paymentmicroservice.Service.*;


@RestController
public class PaymentController {

    @Autowired
    PaymentOptionService paymentOptionService;
    @Autowired
    BankAvailableService bankAvailableService;
    @Autowired
    SummaryService summaryService;
    @Autowired
    CardDetailService cardDetailService;
    @Autowired
    CancelOrderService cancelOrderService;

    @RequestMapping(method = RequestMethod.POST,value = "/checkout")
    public ResponseEntity<CustomResponse> checkOut(@RequestBody Order order)
    {
        return paymentOptionService.getOptions(order);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/payment")
    public ResponseEntity<CustomResponse> payment(@RequestBody PaymentOptionSelected paymentOptionSelected)
    {

        return paymentOptionService.validate(paymentOptionSelected);
    }

    @RequestMapping(method = RequestMethod.GET,value = "/payment/netbanking")
    public ResponseEntity<CustomResponse> netBanking(@RequestBody Order order)
    {
       return bankAvailableService.getBanks(order);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/payment/netbanking")
    public  ResponseEntity<CustomResponse>  getBankName(@RequestBody PaymentOptionSelected paymentOptionSelected)
    {
        return bankAvailableService.validateBank(paymentOptionSelected);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/payment/card")
    public ResponseEntity<CustomResponse> getCard(@RequestBody CardDetail cardDetail)
    {
        return cardDetailService.validateCard(cardDetail);
    }

    @RequestMapping(method = RequestMethod.GET ,value = "/payment/pay")
    public  ResponseEntity<CustomResponse> finalPay(@RequestBody Order order)
    {
        return  summaryService.finalPay(order);
    }

    @RequestMapping(method = RequestMethod.POST,value = "/payment/cancel")
    public ResponseEntity<CustomResponse> cancel(@RequestBody Order order)
    {
       return cancelOrderService.cancelOrder(order);
    }

}
