import { Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder,FormGroup, Validators } from '@angular/forms';
import { User } from 'src/app/model/user.model';
import { SharedService } from 'src/app/services/shared.service';
import { UserService } from 'src/app/services/user.service';
import { ActivatedRoute } from '@angular/router';
import { ResponseApi } from 'src/app/model/response.api';

@Component({
  selector: 'app-user-new',
  templateUrl: './user-new.component.html',
  styleUrls: ['./user-new.component.css']
})
export class UserNewComponent implements OnInit {

  
  contactForm: FormGroup;
  submitted = false;

  user = new User('','','','');
  shared : SharedService;
  message: {};
  classCss:{};

  constructor(
    private userSevice: UserService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
  ) { 
    this.shared = SharedService.getInstance();
    this.createContactForm();
  }
  createContactForm(){
    this.contactForm = this.formBuilder.group({
      password: ['', [Validators.required, Validators.minLength(3)]],  
      email: ['', Validators.required ],
      profile: ['', Validators.required],
      message: ['']
    });
    console.log("construido form: " + this.contactForm.errors);
  }

  ngOnInit() {
    let id: string = this.route.snapshot.params.id;
    if(id != undefined){
      this.findById(id);
    }
  }
  get f() { return this.contactForm.controls; }

  findById(id: string){
    this.userSevice.findById(id).subscribe((responseApi : ResponseApi) => {
      this.user = responseApi.data;
      this.user.password = ''; //clear password
    }, err =>{
        this.showMessage({
          type: 'error',
          text: err['error']['errors'][0]
        });
    });
  }
  register(){
    this.message = {};
    
    this.user.email = this.contactForm.value.email;
    this.user.password = this.contactForm.value.password;
    this.user.profile = this.contactForm.value.profile;
    this.submitted = true;
    if(this.contactForm.invalid){
      
        return ;
    }

    this.userSevice.createOrUpdate(this.user).subscribe((responseApi: ResponseApi) => {
      this.user = new User('','','','');
      let userRet : User = responseApi.data;
      this.createContactForm();
      this.showMessage({
        type:'success',
        text: `Registered ${userRet.email} successfully`
       });
       this.onReset();
      },err =>{
        this.showMessage({
          type: 'error',
          text: err['error']['erros'][0]
        });
      });
  }
  private showMessage(message:{type: string, text: string}) : void {
    this.message = message;
    this.buildClasses(message.type);
    setTimeout(() => {
      this.message = undefined;
    }, 3000);
  }

  private buildClasses(type: string): void {
    this.classCss = {
      'alert' : true
    }
    this.classCss['alert-'+type] = true;
  }
  onReset() {
    this.submitted = false;
    this.contactForm.reset();
}
/*
  getFromGroupClass(isInvalid: boolean, isDirty):{} {
    return{
      'form-group' : true,
      'has-error' : isInvalid && isDirty,
      'has-success' : !isInvalid && isDirty
    }
  } */
}
