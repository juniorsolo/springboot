import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SharedService } from 'src/app/services/shared.service';
import { Injectable } from '@angular/core';

@Injectable({
    providedIn: 'root',
  })
export class  AuthInterceptor implements HttpInterceptor{

    
    constructor(private shared: SharedService) { }
    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        let authRequest : any;
        console.log("authInterceptor: "+ this.shared.isLoggedIn() + "  token: " + this.shared.token );
        if(this.shared.isLoggedIn()){
            authRequest = req.clone({
                setHeaders:{
                    "Authorization" : this.shared.token
                }
            });
            return next.handle(authRequest);
        }else{
            return next.handle(req);
        }
    }

}