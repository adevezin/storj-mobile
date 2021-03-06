import React, { Component } from 'react';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { bucketsContainerActions } from '../reducers/mainContainer/mainReducerActions';
import BucketsComponent from '../components/BucketsComponent';
import FirstSignInComponent from '../components/FirstSignInComponent';
import StorjLib from '../utils/StorjModule';
import ListItemModel from '../models/ListItemModel';
import { getFirstSignIn } from '../utils/AsyncStorageModule';
import { navigateBack } from '../reducers/navigation/navigationActions';

class BucketsContainer extends Component {
    constructor(props) {
        super(props);

        this.state = {
            isLoading: false
        }
    }

    getArraySelectedCount(array) {
        return array.filter((item) => {
            return item.isSelected;
        }).length;
    }
  
    getSelectedBucketsCount() {
        if(!this.props.buckets) return 0;

        return this.getArraySelectedCount(this.props.buckets);
    }

    getSelectedFilesCount() {
        if(!this.props.selectedBucketId || !this.props.files || this.props.files.length === 0) return 0; 

        let openedBucket = this.props.files.find(item => {
            return item.bucketId === this.props.selectedBucketId;
        });

        if(openedBucket) {
            return this.getArraySelectedCount(openedBucket.files);
        }
    }

    async createBucket(name) {
        this.props.setLoading();

        try {
            let createBucketResult = await StorjLib.createBucket(name);

            if(createBucketResult.isSuccess) {
                this.props.createBucket(new ListItemModel(createBucketResult.result));
                this.props.removeFirstSignIn();
            } 
        } catch (e) {
            console.log(e);
        }

        this.props.unsetLoading();
    }

    render() {
        if(this.props.isFirstSignIn) {
            return(
                <FirstSignInComponent
                    removeFirstSignIn = { this.props.removeFirstSignIn }
                    createBucket = { this.createBucket.bind(this)} />
            );
        } else {
            return(
                <BucketsComponent
                    onSingleItemSelected = { this.props.onSingleItemSelected }
                    enableSelectionMode = { this.props.enableSelectionMode }
                    disableSelectionMode = { this.props.disableSelectionMode }
                    isSelectionMode = { this.props.isSelectionMode }
                    isSingleItemSelected = { this.props.isSingleItemSelected }
                    deselectBucket = { this.props.deselectBucket }
                    selectBucket = { this.props.selectBucket }
                    selectedBucketsCount = { this.getSelectedBucketsCount() }
                    selectedFilesCount = { this.getSelectedFilesCount() }
                    buckets = { this.props.buckets }
                    selectedBucketId = { this.props.selectedBucketId }
                    files = { this.props.files }
                    screenName = { this.props.screenName }
                    navigateBack = { this.props.navigateBack } /> 
            );
        }
    }
}

function mapStateToProps(state) {
    let routes = state.bucketsScreenNavReducer.routes;
    let index = state.bucketsScreenNavReducer.index;
    let currentBucketScreenName = routes[index].routeName;

    return {
        isSelectionMode: state.mainReducer.isSelectionMode,        
        buckets: state.mainReducer.buckets,
        isSingleItemSelected: state.mainReducer.isSingleItemSelected,
        isFirstSignIn: state.mainReducer.isFirstSignIn,
        files: state.filesReducer.fileListModels,
        screenName: currentBucketScreenName,
        selectedBucketId: state.mainReducer.openedBucketId
    };
}
    
function mapDispatchToProps(dispatch) {
    return bindActionCreators( { ...bucketsContainerActions, navigateBack }, dispatch);
}

export default connect(mapStateToProps, mapDispatchToProps)(BucketsContainer);

//TODO: Add prop types