# zoophy-services-jumps
Fork of [RESTful Services](https://github.com/ZooPhy/zoophy-services) for [ZooPhy](https://zodo.asu.edu/zoophy/) updated to include support for asymmetric markov jump analysis. This consists of:

1) Retrieving GenBankRecords from the ZooPhy SQL Database

2) Searching GenBankRecords in the ZooPhy Lucene Index

3) Starting/Stopping ZooPhy Pipeline jobs *includes option to run jobs with markov jump analysis*

## Dependencies:
* See [ZooPhy Services](https://github.com/ZooPhy/zoophy-services)
* [BEAST JUMPS](link to repo here)

## Setup:
[See ZooPhy Services setup](https://github.com/ZooPhy/zoophy-services)

## Using Services
See [ZooPhy Services](https://github.com/ZooPhy/zoophy-services) for use of ZooPhy services. 


### Start ZooPhy Job
Updated to include new parameter, useJumps, parsed from Advanced Options run view. 
* Type: POST
* Path: /run
* Required POST Body Data: [JobParameters](src/main/java/edu/asu/zoophy/rest/JobParameters.java) JSON Object containing:
 * replyEmail - String
 * jobName - String (optional)
 * accessions - List of Strings (Limit 1000)
 * useGLM - Boolean (default: false)
 * **useJumps - Boolean (default: false)**
 * predictors - Map of \<String, List of [Predictors](src/main/java/edu/asu/zoophy/rest/pipeline/glm/Predictor.java)> (optional)
   * Note: This is only if custom GLM Predictors need to be used. Otherwise, if usedGLM is set to true, defualt predictors will be used that can only be applied to US States. If locations outside of the US, or more precise locations, are needed then custom predictors must contain at least lat, long, and SampleSize. All predictor values must be positive (< 0) numbers, except for lat/long. Predictor year is not needed, and will not be used for custom predictors. The predictor states must also exactly match the accession states as proccessed in our pipeline, for this reason it is critical to use the [Template Generator service](#generate-glm-predictor-template-download) to generate locations, coordinates, and sample sizes. This feature is currently experimental. 
* Example POST Body:
```
{
  "replyEmail": 'fake@email.com',
  "jobName": 'Australia H1N1 Human HA 09',
  "accessions": ['GQ258462','CY055940','CY055932','CY055788','CY055780','CY055740','CY055661','HQ712184','HM624085'],
  "useGLM": true,
  "useJumps": true, 
  "predictors": {
    "merrylands" : [
                      {"state": "merrylands", "name": "lat", "value": -33.833328, "year": null},
                      {"state": "merrylands", "name": "long", "value": 150.98334, "year": null},
                      {"state": "merrylands", "name": "SampleSize", "value": 2, "year": null}
                   ],
    "perth": [
                {"state": "perth", "name": "lat", "value": -31.95224, "year": null},
                {"state": "perth", "name": "long", "value": 115.8614, "year": null},
                {"state": "perth", "name": "SampleSize", "value": 1, "year": null}
             ],
     "castle-hill" : [
                        {"state": "castle-hill", "name": "lat", "value": -33.73333, "year": null},
                        {"state": "castle-hill", "name": "long", "value": 151.0, "year": null},
                        {"state": "castle-hill", "name": "SampleSize", "value": 4, "year": null}
                     ],
    "brisbane": [
                  {"state": "brisbane", "name": "lat", "value": -27.467939, "year": null},
                  {"state": "brisbane", "name": "long", "value": 153.02809, "year": null},
                  {"state": "brisbane", "name": "SampleSize", "value": 1, "year": null}
                ]
  }
}
```

* Note: The ZooPhy Pipeline ties together several packages of complex software that may fail for numerous reasons. A common reason is having too few or too many unique disjoint Geoname locations (must have between 2 and 50). Jobs may also take very long to run, and time estimates will be provided in update emails. 

### Validate ZooPhy Job
* Type: POST
* Path: /validate
* Required POST Body Data: Exact same as the Run service
* Note: This service is intended to check ZooPhy jobs for common errors before starting the jobs. It will return null if no errors are found, otherwise it returns an error message describing the reason(s) that the job will not succeed. Just because the validation test runs successfully, the job is NOT guaranteed to succeed. 

