import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Ticket } from '../model/ticket.model';
import { HELP_DESK_API} from './helpdesk.api';

@Injectable({
  providedIn: 'root'
})

export class TicketService {

  constructor(private http:HttpClient) { }

  createOrUpdate(ticket: Ticket){
    if(ticket.id != null && ticket.id != ''){
      return this.http.put(`${HELP_DESK_API}/api/ticket`,ticket);
    }else{
      ticket.id = null;
      ticket.status = "New";
      return this.http.post(`${HELP_DESK_API}/api/ticket`,ticket);
    }
  }
}