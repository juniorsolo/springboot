import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { SharedService } from 'src/app/services/shared.service';
import { ActivatedRoute } from '@angular/router';
import { TicketService } from 'src/app/services/ticket.service';
import { ResponseApi } from 'src/app/model/response.api';
import { Ticket } from 'src/app/model/ticket.model';

@Component({
  selector: 'app-ticket-new',
  templateUrl: './ticket-new.component.html',
  styleUrls: ['./ticket-new.component.css']
})
export class TicketNewComponent implements OnInit {


  ticketForm : FormGroup;
  shared : SharedService;
  submitted = false;
  message: {};
  classCss : {};
  imageURL : string;


  constructor(
    private ticketService : TicketService,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder
    ) { 
      this.shared = SharedService.getInstance();
      this.createTicketForm();
    }

  createTicketForm() {
    this.ticketForm = this.formBuilder.group({
      id: [''],
      number: [0],
      title: [''],
      description: [''],
      status: [''],
      priority: [''],
      image: ['']
      /*user: this.formBuilder.group({
        id: [''],
        email: [''],
        password: [''],
        profile: ['']
      }),
      assignedUser: this.formBuilder.group({
        id: [''],
        email: [''],
        password: [''],
        profile: ['']
      }),*/
      //data: [''],
      //changes: [{}]
    });
  }

  ngOnInit() {
    let id : string = this.route.snapshot.params['id'];
    console.log('ngOnInit com id : ' + id);
    if(id != undefined){
      this.findById(id);
    }
  }

  findById(id : string){
    this.ticketService.findById(id).subscribe((responseApi : ResponseApi) => {
        this.ticketForm.patchValue({
          id: responseApi.data.id,
          number: responseApi.data.number,
          title: responseApi.data.title,
          description : responseApi.data.description,
          status: responseApi.data.status,
          priority: responseApi.data.priority,
          image: responseApi.data.image
         /* user: this.formBuilder.group({
            id: responseApi.data.user.id,
            email: responseApi.data.user.email,
            password: responseApi.data.user.password,
            profile: responseApi.data.user.profile
          }),
          assignedUser: this.formBuilder.group({
            id: responseApi.data.assignedUser.id,
            email: responseApi.data.assignedUser.email,
            password: responseApi.data.assignedUser.password,
            profile: responseApi.data.assignedUser.profile
          }),*/
         // data: responseApi.data.data,
         // changes: responseApi.data.changes
        });
    }, err => {
      this.showMessage({
        type: 'error',
        text: err['error']['errors'][0]
      });
    });
  }
  
  register(){
    this.message = {};
    
    this.submitted = true;
    if(this.ticketForm.invalid){ 
        return ;
    }

    this.ticketService.createOrUpdate(this.ticketForm.value).subscribe((responseApi: ResponseApi) => {
      this.onReset();
      let ticketRet : Ticket = responseApi.data;
      this.createTicketForm();
      this.showMessage({
        type:'success',
        text: `Registered ticket number: ${ticketRet.number} with success`
       });
       
      },err =>{
        this.showMessage({
          type: 'error',
          text: err['error']['message']
        });
      });
  }

  onFileChange(event): void {
    if(event.target.files[0].size > 2000000){
      this.showMessage({
        type: 'error',
        text: 'Maximun image size is 2 MB.'
      })
    } else{
     // this.ticketForm.controls.imagem.setValue( '');
      var reader = new FileReader();
       reader.onloadend = (event) => {
         //Preenchendo form control com a imagem
        this.ticketForm.patchValue({
          image: reader.result,         
        });
        //Adicionando URL para o SRC na tela.
        this.imageURL = reader.result as string;
      }
      reader.readAsDataURL(event.target.files[0]);
    }
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
    this.imageURL='';
    this.ticketForm.reset();
  }

  get f() { return this.ticketForm.controls; }
}
