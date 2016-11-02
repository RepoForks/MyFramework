package com.kevadiyak.myframework.other.validation;

import android.widget.EditText;

import com.kevadiyak.rxvalidation.RxValidationResult;
import com.kevadiyak.rxvalidation.Validator;

import java.util.concurrent.TimeUnit;

import rx.Observable;

/*
Example external validator. It could for example make a call to external API to validate email
 */
public class CustomEmailDomainValidator implements Validator<EditText> {

    @Override
    public Observable<RxValidationResult<EditText>> validate(String text, EditText item) {
        RxValidationResult<EditText> result;
        if (text.endsWith("yahoo.in")) {
            result = RxValidationResult.createSuccess(item);
        } else {
            result = RxValidationResult.createImproper(item, "Improper domain");
        }

        return Observable.just(result).delay(2, TimeUnit.SECONDS);
    }
}
