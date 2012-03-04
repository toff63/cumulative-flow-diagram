package net.francesbagual.lean.webapp.cumulative.flow.diagram

import org.springframework.dao.DataIntegrityViolationException

class FeatureController {

    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]

    def index() {
        redirect(action: "list", params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [featureInstanceList: Feature.list(params), featureInstanceTotal: Feature.count()]
    }

    def create() {
        [featureInstance: new Feature(params)]
    }

    def save() {
        def featureInstance = new Feature(params)
        if (!featureInstance.save(flush: true)) {
            render(view: "create", model: [featureInstance: featureInstance])
            return
        }

		flash.message = message(code: 'default.created.message', args: [message(code: 'feature.label', default: 'Feature'), featureInstance.id])
        redirect(action: "show", id: featureInstance.id)
    }

    def show() {
        def featureInstance = Feature.get(params.id)
        if (!featureInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'feature.label', default: 'Feature'), params.id])
            redirect(action: "list")
            return
        }

        [featureInstance: featureInstance]
    }

    def edit() {
        def featureInstance = Feature.get(params.id)
        if (!featureInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'feature.label', default: 'Feature'), params.id])
            redirect(action: "list")
            return
        }

        [featureInstance: featureInstance]
    }

    def update() {
        def featureInstance = Feature.get(params.id)
        if (!featureInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'feature.label', default: 'Feature'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (featureInstance.version > version) {
                featureInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                          [message(code: 'feature.label', default: 'Feature')] as Object[],
                          "Another user has updated this Feature while you were editing")
                render(view: "edit", model: [featureInstance: featureInstance])
                return
            }
        }

        featureInstance.properties = params

        if (!featureInstance.save(flush: true)) {
            render(view: "edit", model: [featureInstance: featureInstance])
            return
        }

		flash.message = message(code: 'default.updated.message', args: [message(code: 'feature.label', default: 'Feature'), featureInstance.id])
        redirect(action: "show", id: featureInstance.id)
    }

    def delete() {
        def featureInstance = Feature.get(params.id)
        if (!featureInstance) {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'feature.label', default: 'Feature'), params.id])
            redirect(action: "list")
            return
        }

        try {
            featureInstance.delete(flush: true)
			flash.message = message(code: 'default.deleted.message', args: [message(code: 'feature.label', default: 'Feature'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'feature.label', default: 'Feature'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
