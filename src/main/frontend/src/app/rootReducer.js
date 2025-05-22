import { combineReducers } from "@reduxjs/toolkit";
import memberReducer from '../modules/member/memberSlice';

const rootReducer = combineReducers({
    member: memberReducer,
});

export default rootReducer;