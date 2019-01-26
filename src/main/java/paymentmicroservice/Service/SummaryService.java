package paymentmicroservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import paymentmicroservice.Models.CustomResponse;
import paymentmicroservice.Models.Order;
import paymentmicroservice.Models.Summary;
import paymentmicroservice.Repository.SummaryRepo;
import paymentmicroservice.Validation.BasicValidation;

import java.util.Date;
import java.util.Optional;

@Service
public class SummaryService {

    @Autowired
    SummaryRepo summaryRepo;
    @Autowired
    MerchantService merchantService;
    @Autowired
    BasicValidation basicValidation;

    public Summary save(Summary summary)
    {
         return summaryRepo.save(summary);
    }

    public float getAmount(String orderId)
    {
        Optional op = summaryRepo.findById(orderId);
        if(op.isPresent())
        {
            Summary summary = (Summary)op.get();
            return summary.getAmount();
        }
        return -1;
    }
    public boolean isOrderIdPresent(String orderId)
    {
        if(summaryRepo.findById(orderId).isPresent())
            return true;
        return false;
    }
    public Optional getSummary(String orderId)
    {
        Optional optional=summaryRepo.findById(orderId);
        return  optional;
    }

    public ResponseEntity<CustomResponse> finalPay(Order order)
    {
        try {
            String TranstionId = merchantService.getTransctionId();
            Date date = merchantService.getDate();
            String status = merchantService.getStatus();
            if (basicValidation.validateString(order.orderId)) {
                Optional optional = getSummary(order.orderId);
                if (optional.isPresent()) {
                    Summary summary = (Summary) optional.get();
                    summary.setTransactionId(TranstionId);
                    summary.setDate(date);
                    summary.setSuccess(status);
                    summary=save(summary);
                    if(summary==null)
                        throw new NullPointerException();
                    return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "OK", null));

                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404, "Not able to save data", null));
        }
        catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404, "Not able to save data", null));

        }

    }
}