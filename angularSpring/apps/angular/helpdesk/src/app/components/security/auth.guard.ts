import { CanActivate, RouterStateSnapshot, ActivatedRouteSnapshot, Router } from '@angular/router';
import { SharedService } from 'src/app/services/shared.service';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';

@Injectable({providedIn:'root'})
export class AuthGuard implements CanActivate{

    public shared: SharedService;

    constructor(private router: Router){
        this.shared = SharedService.getInstance();
    }

    canActivate(route: ActivatedRouteSnapshot, 
                state: RouterStateSnapshot): Observable<boolean> | boolean {
                    console.log("AuthGuard canActivate: " + this.shared.isLoggedIn());
                if(this.shared.isLoggedIn()){
                    return true;
                }
                this.router.navigate(['/login']);
                return false;
    }
}