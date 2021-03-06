import {
    View,
    StyleSheet,
    Animated
} from 'react-native';
import React, { Component } from 'react';
import BucketsScreenNavigation from '../containers/BucketsScreenNavContainer';
import BucketsScreenHeaderComponent from '../components/BucketsScreenHeaderComponent';

export default class BucketsComponent extends Component {
    constructor(props) {
        super(props);

        this.animatedScrollValue = new Animated.Value(0);
    }

    render() {
        return(
            <View style={ styles.mainContainer }>
                <BucketsScreenNavigation
                    animatedScrollValue = { this.animatedScrollValue }  />
                <BucketsScreenHeaderComponent
                    screenName = { this.props.screenName }
                    isSelectionMode = { this.props.isSelectionMode }
                    disableSelectionMode = { this.props.disableSelectionMode }
                    selectedFilesCount = {this.props.selectedFilesCount }
                    selectedBucketsCount = { this.props.selectedBucketsCount }
                    animatedScrollValue = { this.animatedScrollValue }
                    navigateBack = { this.props.navigateBack }
                    buckets = { this.props.buckets }
                    selectedBucketId = { this.props.selectedBucketId } />
            </View>
        );
    }
}

const styles = StyleSheet.create({
    mainContainer: {
        flex: 1,
        backgroundColor: 'white'
    }
});
