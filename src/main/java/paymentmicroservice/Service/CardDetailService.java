package paymentmicroservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import paymentmicroservice.Models.CardDetail;
import paymentmicroservice.Models.CustomResponse;
import paymentmicroservice.Models.Response;
import paymentmicroservice.Models.Summary;
import paymentmicroservice.Repository.CardDetailRepo;
import paymentmicroservice.Validation.CardValidation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardDetailService {

    @Autowired
    CardValidation cardValidation;
    @Autowired
    SummaryService summaryService;
    @Autowired
    CardDetailRepo cardDetailRepo;
    public ResponseEntity<CustomResponse> validateCard(CardDetail cardDetail)
    {
        boolean status=false;
        if(cardDetail.getCardType().equalsIgnoreCase("CreditCard"))
            status=cardValidation.validateCreditCard(cardDetail);
        if(cardDetail.getCardType().equalsIgnoreCase("DebitCard"))
            status=cardValidation.validateDebitCard(cardDetail);
        Response response=new Response();
        List<String> temp=new ArrayList<>();
        Optional op=summaryService.getSummary(cardDetail.getOrderId());
        if(op.isPresent()) {
             Summary summary=(Summary) op.get();
            if (status && (summary.getModOfPayment().equalsIgnoreCase("CreditCard") || summary.getModOfPayment().equalsIgnoreCase("DebiCard"))) {
                temp.add("data Saved Successfully");
                response.orderId = cardDetail.getOrderId();
                response.options = temp;
                cardDetailRepo.save(cardDetail);
                return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "OK", response));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404,"Card details Invalid or OrderId",new Response()));
    }
}
