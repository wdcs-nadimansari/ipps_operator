package com.webclues.IPPSOperator.utility;

import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by webclues on 12/04/2019.
 */

public class Content {


       // public static final String BASE_URL = "";
//       public static final String BASE_URL = "";
       public static final String BASE_URL = "";
    public static final String SERVER_KEY = "";
    public static final String DEVICE_TYPE = "android";


    public static final String PROFILE_IMG = "profile_img";
//    public static final String


    public static final String COUNTRY_CODE = "country_code";
    public static final String COUNTRY_NAME = "country_name";
    public static final String ISO = "iso";

    public static final String TOKEN_ID = "token_id";
    public static final String MESSAGE = "message";

    public static final String USER_DATA = "USER_DATA";
    public static final String IS_LOGIN = "IS_LOGIN";
    public static final String USER_ID = "user_id";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String EMAIL = "email";
    public static final String PHONE_NUMBER = "phone";
    public static final String COMPANY = "company";
    public static final String COMPANY_ID = "company_id";
    public static final String COMPANY_NAME = "company_name";
    public static final String POSITION = "position";
    public static final String POSITION_ID = "position_id";
    public static final String POSITION_NAME = "position_name";
    public static final String PROFILE_PIC = "profile_pic";
    public static final String AUTHORIZATION="Authorization";
    public static final String AUTORIZATION_TOKEN = "oauth_token";
    public static final String OPERATOR_POSITION = "3";
    public static final String DESTROY_AUTH = "yes";
    public static final String ABOUT_US = "about-us";
    public static final String CONTENT = "content";
    public static final String DATE_FORMAT = "dd MMM yyyy";
    public static final String DATANDTIME_FORMAT = "dd MMM yyyy | HH:mm aa";
    public static final String TAG_DateTimeFormate = "dd MMM yyyy | hh:mm a";

    public static final String JOB_ID = "JOB_ID";
    public static final String JOB_STATUS = "JOB_STATUS";
    public static final String JOB_PRIORITY = "JOB_PRIORITY";
    public static String PRIORITY = "PRIORITY";
    public static int FILTER_PRIORITY = Content.ALL;
    public static String STATUS_PRIORITY = "STATUS_PRIORITY";
    public static final String FLAG = "FLAG";
    public static final String JOB = "JOB";
    public static String JOBTYPE = "JOBTYPE";

    public static final String AUTHORIZATIONS = "Authorizations";

    public static final String DEFULT_STRING = "";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String DEVICE_TOKEN = "device_token";
    public static final String DEVICE_ID = "device_id";
    public static final String FIREBASE_UID = "firebase_uid";
    //    Intent


    public static final int REQ_EDIT_PROFILE = 10;
    public static final int REQ_PICK_IMAGE = 11;
    public static final int REQ_JOB_DETAIL = 12;
    public static final int REQ_JOB = 13;
    public static final int QR_CODE_SCAN = 14;
    public static final int REQ_PERMISSION = 1000;


    public final static long ONE_SECOND = 1000;
    public final static long SECONDS = 60;

    public final static long ONE_MINUTE = ONE_SECOND * 60;
    public final static long MINUTES = 60;

    public final static long ONE_HOUR = ONE_MINUTE * 60;
    public final static long HOURS = 24;

    public final static long ONE_DAY = ONE_HOUR * 24;

    //Chat Type

    public static final String CHAT_TEXT_TYPE = "text";
    public static final String CHAT_IMAGE_TYPE = "image";

    //..............................Status code...........................//


    //Status

    public static final String COMPLETED_STATUS = "Completed";
    public static final String DECLINE_STATUS = "Declined";
    public static final String ONGOING_STATUS = "On Going";
    public static final String JOBREQUEST_STATUS = "Job Request";
    public static final String WORKORDER_STARUS = "Work Order";
    public static final String ASSIGNED_STATUS = "Assigned";
    public static final String KIV_STATUS = "Kiv";
    public static final String INCOMPLETE_STATUS = "Incomplete";


    //..............................Jobstatus...........................//

    public static final int JOB_REQUEST = 1;
    public static final int ASSIGNED = 2;
    public static final int ONGOING_WORKORDER = 3;
    public static final int COMPLETED = 4;
    public static final int DECLINE = 5;
    public static final int KIV = 6;
    public static final int INCOMPLETE = 7;
    public static final int ASSIGN_WORKORDER = 3;
    public static final int DECLINE_INCOMPLETE = 5;

    //..............................Filter Priority...........................//

    public static final int PENDING = 0;
    public static final int ALL = 1;
    public static final int LOW = 2;
    public static final int MEDIUM = 3;
    public static final int HIGH = 4;


    //----by uttam place them where they should be
    public static final int MAX_IMAGE_LIMIT = 5;
    public static final String SUBSCRIPTION = "subscription";

    //..............................Notification type...........................//

    public static final String NOTIFICATION_TYPE = "NOTIFICATION_TYPE";
    public static final String JOB_ADDED = "job_added";
    public static final String ENGINEER_REASSIGNED = "engineer_reassigned";
    public static final String JOB_DECLINED = "job_declined";
    public static final String JOB_INCOMPLETED = "job_incompleted";
    public static final String JOB_STARTED = "job_started";
    public static final String JOB_COMPLETED = "job_completed";
    public static final String JOB_ACCEPTED = "job_accepted";
    public static final String ENGINEER_ASSIGNED = "engineer_assigned";
    public static final String PROFILE_UPDATED = "profile_updated";



    public static final String NOTIFICATION = "notification";


    public static boolean isEmpty(final String a) {
        return a.trim().length() == 0;
    }

    public static boolean ValidateEmailTask(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
//        matcher = pattern.matcher(email);
        matcher = Patterns.EMAIL_ADDRESS.matcher(email);
        return matcher.matches();
    }

    public static String millisToLongDHMS(long duration) {
        StringBuffer res = new StringBuffer();
        long temp = 0;
        if (duration >= ONE_SECOND) {
            temp = duration / ONE_DAY;
            if (temp > 0) {
                duration -= temp * ONE_DAY;
                res.append(temp).append(" day");
            }

            temp = duration / ONE_HOUR;
            if (temp > 0) {
                duration -= temp * ONE_HOUR;
                res.append(temp).append(" hour");
            }

            temp = duration / ONE_MINUTE;
            if (temp > 0) {
                duration -= temp * ONE_MINUTE;
                res.append(temp).append(" minute");
            }

            if (!res.toString().equals("") && duration >= ONE_SECOND) {
                res.append(" and ");
            }
//            if (duration > 60) {
//                res.append(temp).append(" Few second");
//            }
            temp = duration / ONE_SECOND;
            if (temp > 0) {
                res.append(temp).append(" second");
            }
            return res.toString();
        } else {
            return "0 second";
        }
    }

}