import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl } from '@angular/forms';
import { SharedService } from 'src/app/services/shared.service';
import { ActivatedRoute } from '@angular/router';

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

  constructor(
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
        status: [''],
        priority: [''],
        imagem: [''],
        user: new FormGroup({
          id: new FormControl(''),
          email: new FormControl(''),
          password: new FormControl(''),
          profile: new FormControl('')
        }),
        assignedUser: new FormGroup({
          id: new FormControl(''),
          email: new FormControl(''),
          password: new FormControl(''),
          profile: new FormControl('')
        }),
        data: [''],
        changes: [{}]
      });
    }
  ngOnInit() {
  }

}
