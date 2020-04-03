package cytomine.core.image

import cytomine.core.command.AddCommand
import cytomine.core.command.Command
import cytomine.core.command.DeleteCommand
import cytomine.core.command.EditCommand
import cytomine.core.command.Transaction
import cytomine.core.security.SecUser
import cytomine.core.utils.ModelService
import cytomine.core.utils.Task
import grails.transaction.Transactional

import static org.springframework.security.acls.domain.BasePermission.READ
import static org.springframework.security.acls.domain.BasePermission.WRITE
@Transactional
class CompanionFileService extends ModelService {

    def cytomineService
    def securityACLService
    def abstractImageService

    def currentDomain() {
        return CompanionFile
    }

    def read(def id) {
        CompanionFile file = CompanionFile.read(id)
        if (file) {
            if (!abstractImageService.hasRightToReadAbstractImageWithProject(file.image)) //TODO: improve
                securityACLService.checkAtLeastOne(file, READ)
        }
        file
    }

    def list(AbstractImage image) {
        if (!abstractImageService.hasRightToReadAbstractImageWithProject(image)) //TODO: improve
            securityACLService.checkAtLeastOne(image, READ)
        CompanionFile.findAllByImage(image)
    }

    def list(UploadedFile uploadedFile) {
        securityACLService.checkAtLeastOne(uploadedFile, READ)
        CompanionFile.findAllByUploadedFile(uploadedFile)
    }

    def add(def json) {
        SecUser currentUser = cytomineService.getCurrentUser()
        securityACLService.checkUser(currentUser)

        Command c = new AddCommand(user: currentUser)
        executeCommand(c, null, json)
    }

    def update(CompanionFile file, def json) {
        securityACLService.checkAtLeastOne(file, WRITE)
        SecUser currentUser = cytomineService.getCurrentUser()

        Command c = new EditCommand(user: currentUser)
        executeCommand(c, file, json)
    }

    def delete(CompanionFile file, Transaction transaction = null, Task task = null, boolean printMessage = true) {
        securityACLService.checkAtLeastOne(file, WRITE)
        SecUser currentUser = cytomineService.getCurrentUser()

        Command c = new DeleteCommand(user: currentUser, transaction: transaction)
        executeCommand(c, file, null)
    }

    def getUploader(def id) {
        CompanionFile file = read(id)
        return file?.uploadedFile?.user
    }

    def getStringParamsI18n(def domain) {
        return [domain.id, domain.originalFilename]
    }
}
