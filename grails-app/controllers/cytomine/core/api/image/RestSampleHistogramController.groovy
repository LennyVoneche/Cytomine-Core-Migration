package cytomine.core.api.image

import cytomine.core.api.RestController
import cytomine.core.image.AbstractImage
import cytomine.core.image.AbstractSlice
import cytomine.core.image.SampleHistogram
import cytomine.core.image.SliceInstance
import cytomine.core.image.UploadedFile
import grails.converters.JSON
//import org.restapidoc.annotation.RestApiMethod
//import org.restapidoc.annotation.RestApiParam
//import org.restapidoc.annotation.RestApiParams
//import org.restapidoc.pojo.RestApiParamType

class RestSampleHistogramController extends RestController {

    def sampleHistogramService
    def imageServerService
    def abstractImageService
    def sliceInstanceService
    def abstractSliceService

//    @RestApiMethod(description = "Get all sample histograms for the given abstract slice", listing = true)
//    @RestApiParams(params = [
//            @RestApiParam(name = "id", type = "long", paramType = RestApiParamType.PATH, description = "The abstract slice id")
//    ])
    def listByAbstractSlice() {
        AbstractSlice abstractSlice = abstractSliceService.read(params.long("id"))
        if (abstractSlice) {
            responseSuccess(sampleHistogramService.list(abstractSlice))
        }
        else {
            responseNotFound("SampleHistogram", "AbstractSlice", params.id)
        }
    }

//    @RestApiMethod(description = "Get all sample histograms for the given slice instance", listing = true)
//    @RestApiParams(params = [
//            @RestApiParam(name = "id", type = "long", paramType = RestApiParamType.PATH, description = "The slice instance id")
//    ])
    def listBySliceInstance() {
        SliceInstance sliceInstance = sliceInstanceService.read(params.long("id"))
        if (sliceInstance) {
            responseSuccess(sampleHistogramService.list(sliceInstance))
        }
        else {
            responseNotFound("SampleHistogram", "SliceInstance", params.id)
        }
    }

//    @RestApiMethod(description="Get a sample histogram")
//    @RestApiParams(params=[
//            @RestApiParam(name="id", type="long", paramType = RestApiParamType.PATH, description = "The sample histogram id")
//    ])
    def show() {
        SampleHistogram sampleHistogram = sampleHistogramService.read(params.long('id'))
        if (sampleHistogram) {
            responseSuccess(sampleHistogram)
        } else {
            responseNotFound("SampleHistogram", params.id)
        }
    }

//    @RestApiMethod(description="Add a new sample Histogram. ")
    def add() {
        add(sampleHistogramService, request.JSON)
    }

//    @RestApiMethod(description="Update a sample Histogram")
//    @RestApiParams(params=[
//            @RestApiParam(name="id", type="long", paramType = RestApiParamType.PATH,description = "The sample Histogram id")
//    ])
    def update() {
        update(sampleHistogramService, request.JSON)
    }

//    @RestApiMethod(description="Delete a sample Histogram)")
//    @RestApiParams(params=[
//            @RestApiParam(name="id", type="long", paramType = RestApiParamType.PATH,description = "The sample Histogram id")
//    ])
    def delete() {
        delete(sampleHistogramService, JSON.parse("{id : $params.id}"),null)
    }
}
