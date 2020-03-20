package cytomine.core.job

/*
* Copyright (c) 2009-2019. Authors: see NOTICE file.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

import cytomine.core.image.AbstractImage
import cytomine.core.image.UploadedFile
import cytomine.core.middleware.ImageServer
import cytomine.core.utils.Version

class ExtractImageMetadataJob {

    def imagePropertiesService
    def sampleHistogramService

    static triggers = {
        simple name: 'extractImageMetadataJob', startDelay: 10000, repeatInterval: 1000*15
    }

    def execute() {
        Version v = Version.getLastVersion()
        if (v?.major >= 2) {
            Collection<AbstractImage> abstractImages = AbstractImage.createCriteria().list(max: 10) {
                createAlias("uploadedFile", "uf")
                and {
                    ne("uf.contentType", "virtual/stack")
                    ne("uf.contentType", "application/zip")
                    or {
                        isNull("bitPerSample")
                        isNull("width")
                        eq("width", -1)
                    }
                    isNull("deleted")
                    isNull("extractedMetadata")
                }
                order("created", "desc")
            }

            abstractImages.each { image ->
                try {
                    ImageServer.withNewSession {
                        UploadedFile.withNewSession {
                            AbstractImage.withNewSession {
                                image.attach()
                                image.uploadedFile.attach()
                                image.uploadedFile.imageServer.attach()

                                log.info "Regenerate properties for image $image - ${image.originalFilename}"
                                try {
                                    imagePropertiesService.regenerate(image)
                                    if (image.bitPerSample > 8)
                                        sampleHistogramService.extractHistogram(image)
                                }
                                catch (Exception e) {
                                    log.error "Error during metadata extraction for image $image: ${e.printStackTrace()}"
                                    image.extractedMetadata = new Date()
                                    image.save(flush: true)
                                }
                            }
                        }
                    }
                }
                catch (Exception e) {
                    log.error "Error during metadata extraction for image $image: ${e.printStackTrace()}"
                }
            }
        }


    }
}
