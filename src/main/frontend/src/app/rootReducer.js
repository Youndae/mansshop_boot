import { combineReducers } from "@reduxjs/toolkit";
import memberReducer from '../features/member/memberSlice';

const rootReducer = combineReducers({
    member: memberReducer,
});

export default rootReducer;