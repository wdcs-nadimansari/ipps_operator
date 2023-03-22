package com.webclues.IPPSOperator.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.webclues.IPPSOperator.service.APIClient
import com.webclues.IPPSOperator.Modelclass.JobDetailModel
import com.webclues.IPPSOperator.R
import com.webclues.IPPSOperator.adapter.JobImageViewpagerAdapter
import com.webclues.IPPSOperator.service.checkNetworkState
import com.webclues.IPPSOperator.utility.Content
import com.webclues.IPPSOperator.utility.CustomProgress
import com.webclues.IPPSOperator.utility.Utility
import com.webclues.IPPSOperator.service.ApiInterface
import kotlinx.android.synthetic.main.activity_job_detail.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class JobDetailActivity : AppCompatActivity(), View.OnClickListener {


    private var dotscount: Int = 0
    var dots: Array<ImageView?> = arrayOf()
    lateinit var viewpagerAdapter: JobImageViewpagerAdapter
    lateinit var JobStatus: String
    var priority: Int = 0
    var status: Int = 0
    lateinit var JobPrioriry: String
    lateinit var customProgress: CustomProgress
    lateinit var apiInterface: ApiInterface
    var Jobid: Int = 0
    lateinit var context: Context
    lateinit var jobDetailModel: JobDetailModel
    var changedStatus = false
    var jobtype: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)
        context = this
        initview()
    }

    private fun initview() {

        customProgress = CustomProgress.instance

        if (intent.extras != null) {
            Jobid = intent.getIntExtra(Content.JOB_ID, 0)
            status = intent.getIntExtra(Content.JOB_STATUS, 0)
            priority = intent.getIntExtra(Content.JOB_PRIORITY, 0)

        }

        if (intent.hasExtra(Content.JOB)) {
            jobtype = intent.getStringExtra(Content.JOBTYPE)
        }


        ivBack.setOnClickListener(this)

        if (checkNetworkState(context)) {
            jobdetail(Jobid)
        } else {
            Utility().showOkDialog(
                context,
                resources.getString(R.string.app_name),
                resources.getString(R.string.error_internet_ln)
            )
        }

    }


    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.ivBack -> {
                onBackPressed()
            }
        }
    }

    private fun jobdetail(jobid: Int) {

        customProgress.showProgress(this, getString(R.string.please_wait), false)
        apiInterface = APIClient.getretrofit(this).create(ApiInterface::class.java)
        val call: Call<JsonObject> = apiInterface.jobdetails(jobid)
        call.enqueue(object : Callback<JsonObject> {


            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                customProgress.hideProgress()
                val jsonObject = JSONObject(response.body().toString())
                val statuscode = jsonObject.optInt("status_code")
                if (statuscode == 200) {
                    if (jsonObject.optBoolean("status")) {
                        val data = jsonObject.optJSONObject("data")
                        if (data != null) {
                            changedStatus = true
                            jobDetailModel =
                                Gson().fromJson(data.toString(), JobDetailModel::class.java)
                            SetJobDetailData(jobDetailModel)
                        }
                    } else {
                        Utility().showOkDialog(
                            context,
                            resources.getString(R.string.app_name),
                            jsonObject.optString("message")
                        )
                    }
                } else if (statuscode == 403) {
                    Utility().showInactiveDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                } else {
                    Utility().showOkDialog(
                        context,
                        resources.getString(R.string.app_name),
                        jsonObject.optString("message")
                    )
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {

                customProgress.hideProgress()
                Utility().showOkDialog(
                    context,
                    resources.getString(R.string.app_name),
                    getString(R.string.error_something_is_wrong_ln)
                )

            }

        })

    }

    private fun SetPriority(jobDetailModel: JobDetailModel) {


        if (jobDetailModel.priority == Content.ALL) {
            JobPrioriry = getString(R.string.all)

        } else if (jobDetailModel.priority == Content.PENDING) {
            JobPrioriry = getString(R.string.pending)
        } else if (jobDetailModel.priority == Content.LOW) {
            JobPrioriry = getString(R.string.low)


        } else if (jobDetailModel.priority == Content.MEDIUM) {
            JobPrioriry = getString(R.string.medium)


        } else if (jobDetailModel.priority == Content.HIGH) {
            JobPrioriry = getString(R.string.high)

        }

    }

    private fun SetJobDetailData(jobDetailModel: JobDetailModel) {     //SetJobdetail Data call


        SetPriority(jobDetailModel)


        if (jobDetailModel.job_status == Content.JOB_REQUEST) {
            JobStatus = getString(R.string.job_request)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.fontColorRegularGrey))
            txtStatus.setBackgroundResource(R.drawable.rounded_lightgrey)
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtMachineName.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name
            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.text = jobDetailModel.comment
            }


            txtJobStartTime.visibility = View.GONE
            llEngineerName.visibility = View.GONE
            llJobDuration.visibility = View.GONE
        } else if (jobDetailModel.job_status == Content.KIV) {
            JobStatus = getString(R.string.kiv)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.bluelight))
            txtStatus.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)

            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtMachineName.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.text = jobDetailModel.comment
            }

            txtPriority.visibility = View.VISIBLE
            llEngineerName.visibility = View.GONE
            txtJobStartTime.visibility = View.GONE
            llJobDuration.visibility = View.GONE

        } else if (jobDetailModel.job_status == Content.DECLINE) {
            JobStatus = getString(R.string.decline)
            txtStatus.setBackgroundResource(R.drawable.round_lightred_bg)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            llMetalDesc.setBackgroundColor(ContextCompat.getColor(this, R.color.extraLightGrey))
            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtDeclineComment.text = jobDetailModel.decline_reason
            txtMachineName.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name

            txtDeclineBy.text =
                context.resources.getString(R.string.decline_by) + " " + jobDetailModel.declined_by + ":"
            txtDeclineByUser.text = jobDetailModel.declined_by_user
            txtDeclineReason.text = jobDetailModel.decline_reason

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.text = jobDetailModel.comment
            }


            llComments.visibility = View.GONE
            llJobDuration.visibility = View.GONE
            llEngineerName.visibility = View.GONE
            txtJobStartTime.visibility = View.GONE
            view.visibility = View.VISIBLE
            llDeclineBy.visibility = View.VISIBLE

        } else if (jobDetailModel.job_status == Content.INCOMPLETE) {
            JobStatus = getString(R.string.incomplete)
            txtStatus.setBackgroundResource(R.drawable.round_lightred_bg)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.red))
            llMetalDesc.setBackgroundColor(ContextCompat.getColor(this, R.color.extraLightGrey))
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)

            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)
            txtJobStartTime.text = Utility.getDateTime(jobDetailModel.job_start_time.toLong())

            txtEngineerComment.text = jobDetailModel.incomplete_reason
            txtMachineName.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name
            txtEngineerName.text = jobDetailModel.engineer_name

            txtIncompleteReason.text = jobDetailModel.incomplete_reason
            txtJobDuration.text = jobDetailModel.job_duration


            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.text = jobDetailModel.comment
            }


            llJobDuration.visibility = View.VISIBLE
            llEngineerName.visibility = View.VISIBLE
            llJobStartTime.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
            llIncompleteReason.visibility = View.VISIBLE


        } else if (jobDetailModel.job_status == Content.ONGOING_WORKORDER) {
            JobStatus = getString(R.string.work_order)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            txtStatus.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)

            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtJobStartTime.text = Utility.getDateTime(jobDetailModel.job_start_time.toLong())

            txtMachineName.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name
            txtEngineerName.text = jobDetailModel.engineer_name

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.text = jobDetailModel.comment
            }


            llJobStartTime.visibility = View.VISIBLE
            llEngineerName.visibility = View.VISIBLE
            llJobDuration.visibility = View.GONE


        } else if (jobDetailModel.job_status == Content.ASSIGNED) {
            JobStatus = getString(R.string.assigned)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.yellow))
            txtStatus.setBackgroundResource(R.drawable.round_lightblue_bg)
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)

            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtEngineerName.text = jobDetailModel.engineer_name
            txtMachineName.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name

            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.text = jobDetailModel.comment
            }


            llJobDuration.visibility = View.GONE
            llEngineerName.visibility = View.VISIBLE

        } else if (jobDetailModel.job_status == Content.COMPLETED) {
            JobStatus = getString(R.string.completed)
            txtStatus.setTextColor(ContextCompat.getColor(this, R.color.green))
            txtStatus.setBackgroundResource(R.drawable.round_lightgreen_bg)
            txtStatusPriority.setBackgroundResource(R.drawable.round_lightblue_bg)
            llMetalDesc.setBackgroundColor(ContextCompat.getColor(this, R.color.extraLightGrey))

            txtOperatorName.text =
                jobDetailModel.created_user_name + " | " + Utility.getDateTime(jobDetailModel.created_time)

            txtJobStartTime.text = Utility.getDateTime(jobDetailModel.job_start_time.toLong())

            txtJobDuration.text = jobDetailModel.job_duration
            txtEngineerName.text = jobDetailModel.engineer_name
            txtMachineName.text = jobDetailModel.machine_name
            txtLocation.text = jobDetailModel.location_name
            txtProblem.text = jobDetailModel.problem_name


            if (!jobDetailModel.comment.isNullOrEmpty()) {
                llDescriptions.visibility = View.VISIBLE
                txtCommentDesc.text = jobDetailModel.comment
            }


            llJobStartTime.visibility = View.VISIBLE
//            txtJobStartTime.visibility = View.VISIBLE
            llJobDuration.visibility = View.VISIBLE


        }
        txtStatus.text = JobStatus
        txtStatusPriority.text = JobPrioriry



        viewpagerAdapter = JobImageViewpagerAdapter(this, jobDetailModel.images)
        ivViewPager.adapter = viewpagerAdapter
        SetImagecount()
    }


    private fun SetImagecount() {
        dotscount = viewpagerAdapter.count
        dots = arrayOfNulls<ImageView>(dotscount)
        for (i in 0..dotscount - 1) {
            dots[i] = ImageView(this)
            dots[i]!!.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.non_active_dot
                )
            )

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            params.setMargins(8, 0, 8, 0)

            Indicator.addView(dots[i], params)
        }


        dots[0]!!.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.non_active_dot
            )
        )
        ivViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                for (i in 0 until dotscount) {
                    dots[i]!!.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.non_active_dot
                        )
                    )
                }

                dots[position]!!.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.active_dots
                    )
                )
            }

        })
    }

    override fun onBackPressed() {
        if (changedStatus) {
            val intent = Intent()
            intent.putExtra("JobType", Content.JOBTYPE)
            setResult(Activity.RESULT_OK, intent)
            finish()
        } else {
            super.onBackPressed()
        }
    }
}
