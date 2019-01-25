package paymentmicroservice.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import paymentmicroservice.Models.*;
import paymentmicroservice.Repository.CodSupportRepo;
import paymentmicroservice.Repository.PaymentOptionRepo;
import paymentmicroservice.Repository.SummaryRepo;

import java.util.*;

@Service
public class PaymentOptionService {

    @Autowired
    PaymentOptionRepo paymentOptionRepo;
    @Autowired
    CodSupportRepo codSupportRepo;
    @Autowired
    SummaryService summaryService;
    private static final String cod = "COD";
    private static final float codBase = 50000;
    private static final float emiBase=3000;
    private static final String emi="EMI";

      //Controller Function
    public ResponseEntity<CustomResponse> getOptions(CheckOut checkOut)
    {
          List<String> productIds = checkOut.getProductsId();
          List<Integer> qty = checkOut.getQuantity();
          String orderId = checkOut.getOrderId();
          float amount = checkOut.getAmount();
          //API of catalogue

          List<String> paymentOptions=getPaymentOption(productIds,amount);
          Summary summary = new Summary(orderId,null,null,null,amount,null);
          summaryService.save(summary);
          Response response = new Response();
          response.options=paymentOptions;
          response.orderId = orderId;
          return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200,"OK",response));
    }
    // Controller Function
    public ResponseEntity<CustomResponse> validate(PaymentOptionSelected paymentOptionSelected)
    {
        float amount = summaryService.getAmount(paymentOptionSelected.orderId);
        String optionSelected = paymentOptionSelected.optionSelected;
        List<String> productIds=new ArrayList<>();
        //MS4 Api call
        productIds.add("1");
        List<String> paymentOptions=getPaymentOption(productIds,amount);
        List<String> temp = new ArrayList<>();

        Response response = new Response();
        response.orderId =paymentOptionSelected.orderId;

       if(paymentOptions.contains(optionSelected))
        {
            temp.add(optionSelected);
            response.options =temp;
            Summary summary=new Summary(paymentOptionSelected.orderId,optionSelected,null,null,amount,null);
            summaryService.save(summary);
            return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(200,"OK",response));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new CustomResponse(400,"You have selected an incorrect option",new Response()));
    }

     //
    public List<String> getPaymentOption(List<String>productIds,float amount)
    {
        List<String> paymentOptions=paymentOptionRepo.getOptions();
        if(hasCOD(paymentOptions)&& (codNotAvailable(productIds)||codBase<amount))
        {
            paymentOptions.remove(cod);
        }
        if(amount<emiBase&&paymentOptions.contains(emi))
        {
            paymentOptions.remove(emi);
        }
        return paymentOptions;
    }

    public boolean hasCOD(List<String>paymentOptions)
    {
        for(String paymentOption:paymentOptions)
        {
            if(paymentOption.equalsIgnoreCase(cod))
                return true;
        }
        return false;
    }


    public boolean codNotAvailable(List<String>productIds)
    {
        for (String id:productIds)
        {
            if(codSupportRepo.findById(id).isPresent())
                return true;
        }
        return false;
    }



}
