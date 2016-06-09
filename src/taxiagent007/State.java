/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxiagent007;

/**
 *
 * @author kotaro and Fabio :)
 */
public enum State {
    WAITING_FOR_CALLS,
    WAITING_FOR_COMPANY,
    TAKING_PASSENGER,
    GOING_FOR_PASSENGER,
    PICKING_PASSENGER,
    DROPING_PASSENGER,
    WAITING_FOR_BIDS,
    PREPARING,
    WAITING_DRIVER_RESPONSE,
    BIDDING_FOR_PASSENGER,
    WAITING_FOR_COMPANY_DECISION,
    WON_BID_RESTING,
    OUT_OF_SERVICE,
    GOING_HOME
}
