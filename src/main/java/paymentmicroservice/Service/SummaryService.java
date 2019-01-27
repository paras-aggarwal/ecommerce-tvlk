package paymentmicroservice.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import paymentmicroservice.Models.CheckOut;
import paymentmicroservice.Models.CustomResponse;
import paymentmicroservice.Models.Order;
import paymentmicroservice.Models.Summary;
import paymentmicroservice.Repository.SummaryRepo;
import paymentmicroservice.Validation.BasicValidation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SummaryService {

    @Autowired
    SummaryRepo summaryRepo;
    @Autowired
    MerchantService merchantService;
    @Autowired
    BasicValidation basicValidation;
    @Autowired
    CancelOrderService cancelOrderService;

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
    public boolean validateOrderId(String orderId){
        if(basicValidation.validateString(orderId)){
            Optional op =getSummary(orderId);
            if(op.isPresent()){
                Summary summary=(Summary)op.get();
                if(summary.getStatus()!=null&&summary.getStatus().equalsIgnoreCase("Success"))
                    return true;
            }

        }
        return false;
    }

    public ResponseEntity<CustomResponse> finalPay(Order order)
    {
        try {

            System.out.println("yha aaya");
            String TranstionId = merchantService.getTransctionId();
            Date date = merchantService.getDate();
            String status = merchantService.getStatus();
            if (basicValidation.validateString(order.orderId)) {
                Optional optional = getSummary(order.orderId);
                if (optional.isPresent()) {
                    Summary summary = (Summary) optional.get();
                    if(summary.getModOfPayment()==null)
                        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404, "You haven't selected Mode of payment", null));
                    summary.setTransactionId(TranstionId);
                    summary.setDate(date);
                    summary.setSuccess(status);
                    summary=save(summary);
                    if(summary!=null&&sendResponse(summary))
                        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200, "OK", null));
                    else{
                        cancelOrderService.cancelOrder(order.orderId);
                        summary.setSuccess("Refund Initiated");
                        summary=save(summary);
                        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404, "Payment failed!", null));

                    }

                }
            }
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404, "Not able to save data", null));
        }
        catch (NullPointerException e) {
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(404, "Not able to save data", null));

        }

    }

    public boolean sendResponse(Summary summary)
    {
        try {
            final String uri = "http://d034eb08.ngrok.io/orderConfirmation";
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Summary> entity = new HttpEntity<>(summary,headers);
            String response = restTemplate.postForObject(uri, entity, String.class);
            System.out.println(response);
            JSONObject obj = new JSONObject(response);
            int statusCode = obj.getInt("statusCode");

            ObjectMapper mapper = new ObjectMapper();

            System.out.println(statusCode);
            if(statusCode==200)
                return true;
            return false;

        }
        catch (Exception e){
            System.out.println(e);
            return false;
        }

    }

}

