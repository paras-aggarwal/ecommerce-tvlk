package paymentmicroservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import paymentmicroservice.Models.*;
import paymentmicroservice.Repository.BankAvailableRepo;
import paymentmicroservice.Repository.SummaryRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BankAvailableService {

    @Autowired
    BankAvailableRepo bankAvailableRepo;
    @Autowired
    SummaryService summaryService;
    public ResponseEntity<CustomResponse> getBanks(Order order)
    {
        Response response=new Response();
        if(!validateNetbanking(order.orderId))
           return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404,"You have not selected NetBanking",new Response()));
        response.options=bankAvailableRepo.getAllBankS();
        response.orderId =order.orderId;
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200,"OK",response));
    }

    public boolean validateNetbanking(String orderId)
    {
        Optional optional =summaryService.getSummary(orderId);
        if(optional.isPresent()) {
            Summary summary = (Summary)optional.get();
            if (summary.getModOfPayment().equalsIgnoreCase("NetBanking")) {
                return true;
            }
        }
        return false;
    }

    public ResponseEntity<CustomResponse> validateBank(PaymentOptionSelected paymentOptionSelected)
    {
        Response response=new Response();
        if(!validateNetbanking(paymentOptionSelected.orderId))
           return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404,"You have not selected NetBanking",new Response()));
        response.orderId=paymentOptionSelected.orderId;
        BankAvailable bankAvailable=bankAvailableRepo.findByName(paymentOptionSelected.optionSelected);
        List<String> temp = new ArrayList<>();
        if(bankAvailable==null)
        {
           return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404,"Please select from the given options",new Response()));
        }
        temp.add(paymentOptionSelected.optionSelected);
        response.options=temp;
       return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200,"Ok",response));
    }


}
